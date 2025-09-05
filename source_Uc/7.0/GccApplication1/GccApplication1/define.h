// define.h
/*
 * define.h
 *
 * Created on: 10.03.2017
 * Author: Robert
 */



#ifndef DEFINE_H_
#define DEFINE_H_

#include <avr/pgmspace.h>

#define EEADDR_A 0x010
#define OUTA_ON  1
#define OUTA_OFF 0

#define EEADDR_E 0x015
#define CLIP_ON  0
#define CLIP_OFF 1


#define EEADDR_B 0x020
#define OUTB_ON  1
#define OUTB_OFF 0

#define EEADDR_W 0x025
#define WEBASTO_ON  1
#define WEBASTO_OFF 0

#define EEADDR_C 0x030
#define RAPORT_ON  0
#define RAPORT_OFF 1


#define EEADDR_D 0x035
#define SIGNAL_L 0
#define SIGNAL_H 1


#define EEADDR_I 0x040
#define INPUT_OFF 1
#define INPUT_ON 0


#define MOJ_BUFOR 69

char buf[MOJ_BUFOR];

char buf_kopia[MOJ_BUFOR];

char uart_buf[80];




char* wsk2;
char* wsk1;





#define  OUT_A_OFF        PORTD &=~(1<<PD6)
#define  OUT_A_ON         PORTD |=(1<<PD6)


#define  OUT_B_OFF        PORTD &=~(1<<PD7)
#define  OUT_B_ON         PORTD |=(1<<PD7)




#define  LED_OFF  PORTD |=(1<<PD2)
#define  LED_SYS_ON   PORTD &=~(1<<PD2)

#define  PWR_GSM_OFF    PORTC |=(1<<PC1)
#define  PWR_GSM_ON     PORTC &=~(1<<PC1)

#define  KEY_DOWN !(PINC & (1<<PC2))



char flaga;

#define  PORTD_USTAW_WYJSCIOWY DDRD |= (1<<PD6)|(1<<PD7)|(1<<PD2)

#define  PORTC_USTAW_WYJSCIOWY DDRC |= (1<<PC1)



#define EEADDR_F ((uint8_t*)6) // Przykładowy adres, dostosuj, jeśli już używany
#define OUT2_MODE_TIMED 0
#define OUT2_MODE_ALWAYS_ON 1


#define EEPROM_ADDR_GLOBAL_2 0x050


#endif /* DEFINE_H_ */
