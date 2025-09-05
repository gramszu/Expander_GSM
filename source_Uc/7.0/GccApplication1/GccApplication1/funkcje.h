// funkcje.h
/*
 * funkcje.h
 *
 * Created on: 10.03.2017
 * Author: Robert
 */

#ifndef FUNKCJE_H_
#define FUNKCJE_H_


void temp();
void raport_temp(void);
void raport_signal(void);
void signal_zapis(uint8_t stan);
void signal_odczyt(void);
void clip_zapis(uint8_t stan);
void clip_odczyt(void);
void blink (void);
void reset_GSM(void);
void reset_power_GSM(void);
void analizuj_dane( char*buf );
void zapis_stanu_wyjsca_a(uint8_t stan);
void zapis_stanu_wyjsca_b(uint8_t stan);
void raport_zapis_konfiguracji(uint8_t stan);
void raport_odczyt_konfiguracji();
void raport_odczyt_konfiguracji_RG();
void odczyt_stanu_wyjscia_a(void);
void odczyt_stanu_wyjscia_b(void);
void rejestracja_sieci(void);
void soft_timer_init(void);
void raport (void);
void arow_a (void);
void arow_b (void);
void send_in_alarm(void);
void in (void);
void zapis_webasto(uint8_t stan);
void odczyt_webasto (void);
void numer (void);
void raport_clip (void);
void clip_raportuj(void);
void raport_globalny(void);
void zapis_input (uint8_t stan);
void odczyt_input(void);
void odczyt_input_raport(void);
void spy (void);
void blink_signal_warning(void);
void check_signal_and_blink(void);
void blink_long(void);
void blink_fast(void);
void reset(void);
void out2_timed_on(uint32_t seconds);  // uint32_t dla zakresu 1-99999 sekund
void raport_stanu_wyjscia_a(void);




#endif /* FUNKCJE_H_ */
