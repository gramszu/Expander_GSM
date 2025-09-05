/*
 * mkuart.c
 *
 *  Created on: 2010-09-04
 *       Autor: Miros³aw Kardaœ
 */
#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/pgmspace.h>
#include <stdlib.h>
#include <util/delay.h>
#include <util/atomic.h>

#include "mkuart.h"

volatile uint8_t ascii_line;

// definiujemy w koñcu nasz bufor UART_RxBuf
volatile char UART_RxBuf[UART_RX_BUF_SIZE];
// definiujemy indeksy okreœlaj¹ce iloœæ danych w buforze
volatile uint8_t UART_RxHead; // indeks oznaczaj¹cy „g³owê wê¿a”
volatile uint8_t UART_RxTail; // indeks oznaczaj¹cy „ogon wê¿a”

// definiujemy w koñcu nasz bufor UART_TxBuf
volatile char UART_TxBuf[UART_TX_BUF_SIZE];
// definiujemy indeksy okreœlaj¹ce iloœæ danych w buforze
volatile uint8_t UART_TxHead; // indeks oznaczaj¹cy „g³owê wê¿a”
volatile uint8_t UART_TxTail; // indeks oznaczaj¹cy „ogon wê¿a”

// wskaŸnik do funkcji callback dla zdarzenia UART_RX_STR_EVENT()
static void (*uart_rx_str_event_callback)(char * pBuf);

// funkcja do rejestracji funkcji zwrotnej w zdarzeniu UART_RX_STR_EVENT()
void register_uart_str_rx_event_callback(void (*callback)(char * pBuf)) {
    uart_rx_str_event_callback = callback;
}

// wskaŸnik do funkcji callback0 dla zdarzenia UART_RX_STR_EVENT()
static uint8_t (*uart_rx_str_event_callback0)(char * pBuf);

// funkcja do rejestracji funkcji zwrotnej w zdarzeniu UART_RX_STR_EVENT()
void register_uart_str_rx_event_callback0(uint8_t (*callback)(char * pBuf)) {
    uart_rx_str_event_callback0 = callback;
}

// Zdarzenie do odbioru danych ³añcucha tekstowego z bufora cyklicznego
void UART_RX_STR_EVENT(char * rbuf) {
    if( ascii_line ) {
        if( uart_rx_str_event_callback0 || uart_rx_str_event_callback ) {
            uart_get_str( rbuf );
            uint8_t res = 1;
            if( rbuf[0] ) {
                if( uart_rx_str_event_callback0 ) {
                    res = (*uart_rx_str_event_callback0)( rbuf );
                }
                if( res && uart_rx_str_event_callback ) {
                    (*uart_rx_str_event_callback)( rbuf );
                }
            }
        } else UART_RxHead = UART_RxTail;
    }
}

void USART_Init( uint16_t baud ) {
    /* Ustawienie prêdkoœci */
    UBRR0H = (uint8_t)(baud>>8);
    UBRR0L = (uint8_t)baud;
    /* Za³¹czenie nadajnika I odbiornika */
    UCSR0B = (1<<RXEN0)|(1<<TXEN0);
    /* Ustawienie format ramki: 8bitów danych, 1 bit stopu */
    UCSR0C = (1<<UCSZ01)|(1<<UCSZ00);

    // jeœli korzystamy z interefejsu RS485
    #ifdef UART_DE_PORT
        // inicjalizujemy liniê steruj¹c¹ nadajnikiem
        UART_DE_DIR |= UART_DE_BIT;
        UART_DE_ODBIERANIE;
    #endif

    // jeœli korzystamy z interefejsu RS485
    #ifdef UART_DE_PORT
        // jeœli korzystamy z interefejsu RS485 za³¹czamy dodatkowe przerwanie TXCIE
        UCSR0B |= (1<<RXEN0)|(1<<TXEN0)|(1<<RXCIE0)|(1<<TXCIE0);
    #else
        // jeœli nie korzystamy z interefejsu RS485
        UCSR0B |= (1<<RXEN0)|(1<<TXEN0)|(1<<RXCIE0);
    #endif
}

// procedura obs³ugi przerwania Tx Complete, gdy zostanie opóŸniony UDR
// kompilacja gdy u¿ywamy RS485
#ifdef UART_DE_PORT
ISR( USART0_TX_vect ) {
    UART_DE_ODBIERANIE;    // zablokuj nadajnik RS485
}
#endif

// definiujemy funkcjê dodaj¹c¹ jeden bajt do bufora cyklicznego
void uart_putc( char data ) {
    uint8_t tmp_head;
    ATOMIC_BLOCK( ATOMIC_RESTORESTATE ) {
        tmp_head = (UART_TxHead + 1) & UART_TX_BUF_MASK;
    }
    // pêtla oczekuje je¿eli brak miejsca w buforze cyklicznym na kolejne znaki
    while ( tmp_head == UART_TxTail ){}

    UART_TxBuf[tmp_head] = data;
    UART_TxHead = tmp_head;

    // inicjalizujemy przerwanie wystêpuj¹ce, gdy bufor jest pusty
    UCSR0B |= (1<<UDRIE0);
}

void uart_puts(char *s) {
    register char c;
    while ((c = *s++)) uart_putc(c);
}

void uart_puts_P( const char *s ) {
    register char c;
    while ((c = pgm_read_byte(s++))) uart_putc(c);
}

void uart_putint(int value, int radix) {
    char string[17];
    itoa(value, string, radix);
    uart_puts(string);
}

// procedura obs³ugi przerwania nadawczego, pobieraj¹ca dane z bufora cyklicznego
ISR( USART0_UDRE_vect ) {
    if ( UART_TxHead != UART_TxTail ) {
        UART_TxTail = (UART_TxTail + 1) & UART_TX_BUF_MASK;
#ifdef UART_DE_PORT
        UART_DE_NADAWANIE;
#endif
        UDR0 = UART_TxBuf[UART_TxTail];
    } else {
        UCSR0B &= ~(1<<UDRIE0);
    }
}

// funkcja pobieraj¹ca jeden bajt z bufora cyklicznego
int uart_getc(void) {
    int data = -1;
    if ( UART_RxHead == UART_RxTail ) return data;
    ATOMIC_BLOCK( ATOMIC_RESTORESTATE ) {
        UART_RxTail = (UART_RxTail + 1) & UART_RX_BUF_MASK;
        data = UART_RxBuf[UART_RxTail];
    }
    return data;
}

char * uart_get_str(char * buf) {
    int c;
    char * wsk = buf;
    if( ascii_line ) {
        while( (c = uart_getc()) ) {
            if( 13 == c || c < 0) break;
            *buf++ = c;
        }
        *buf=0;
        ascii_line--;
    }
    return wsk;
}

char uart_wait_char( uint16_t time_out_ms ) {
    int res;
    if( time_out_ms < 1 ) time_out_ms = 10;
    do {
        res = uart_getc();
        if( res > -1 || !time_out_ms ) break;
        else if( time_out_ms-- ) _delay_ms(1);
    } while( res < 0 );
    if( res<0 ) res = 0;
    return res;
}

// procedura obs³ugi przerwania odbiorczego, zapisuj¹ca dane do bufora cyklicznego
ISR( USART0_RX_vect ) {
    register uint8_t tmp_head;
    register char data;

    data = UDR0; // pobieramy natychmiast bajt danych z bufora sprzêtowego

    tmp_head = ( UART_RxHead + 1) & UART_RX_BUF_MASK;

    if ( tmp_head == UART_RxTail ) {
        UART_RxHead = UART_RxTail;
    } else {
        if( data ) {
            if( 13 == data ) ascii_line++;
            if( 10 != data ) {
                UART_RxHead = tmp_head;
                UART_RxBuf[tmp_head] = data;
            }
        }
    }
}
