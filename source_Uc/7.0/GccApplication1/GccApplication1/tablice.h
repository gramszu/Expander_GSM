// tablice.h
/*
 * tablice.h
 *
 * Created on: 20.05.2017
 * Author: Robert
 */

#include <avr/pgmspace.h>

#ifndef TABLICE_H_
#define TABLICE_H_


const char RAPORT [] PROGMEM = "RAPORT";
const char A_ON   [] PROGMEM = "OUT1#ON";
const char A_OFF  [] PROGMEM = "OUT1#OFF";
const char B_ON   [] PROGMEM = "OUT2#ON"; // Ta komenda będzie teraz obsługiwać oba tryby
const char B_OFF  [] PROGMEM = "OUT2#OFF";
const char A_AROW [] PROGMEM = "OUT1#STE";//off
const char B_AROW [] PROGMEM = "OUT2#STE";//off
const char ALL_ON [] PROGMEM = "ALL#ON";
const char ALL_OFF [] PROGMEM = "ALL#OFF";
//const char AUTO_ON[]   PROGMEM = "RsssT#ON";    // Włączenie auto-raportowania
//const char AUTO_LOCK[] PROGMEM = "Rss1T#LOCK";  // Wyłączenie auto-raportowania




const char ALARM_ON [] PROGMEM =  "ALARM#ON";
const char ALARM_OFF[] PROGMEM =  "ALARM#LOCK";
const char CLPON [] PROGMEM = "CLP#ON";
const char CLPOFF[] PROGMEM = "CLP#LOCK";
const char SPY       [] PROGMEM ="#SPY#";



const char OK   [] PROGMEM = "OK";
const char RING [] PROGMEM = "RING";
const char CMGS   [] PROGMEM ="AT+CMGS=";
const char ATCREG [] PROGMEM="AT+CREG?\r";
const char ATCNMI [] PROGMEM="AT+CNMI=2,2,0,1\r";
const char ATCMGF [] PROGMEM="AT+CMGF=1\r";
const char ATCMIC [] PROGMEM="AT+CMIC=0,14\r";
const char ATCOLP [] PROGMEM="AT+COLP=0\r";
const char ATCMGDA [] PROGMEM="AT+CMGDA= DEL ALL\r";
const char ATE0 [] PROGMEM="AT\r";




const char ATCSQ  [] PROGMEM ="AT+CSQ\r";
const char AT []   PROGMEM =  "AT\r";
const char ATH []  PROGMEM = "ATH\r";
const char ATZ []  PROGMEM =  "ATZ\r";
const char ATD []  PROGMEM ="ATD";
const char ALARM [] PROGMEM ="*Alarm Expander GSM*";
const char SZPIEG[] PROGMEM ="**Spy*>>>  ";
const char ATCLIP []PROGMEM = "AT+CLIP=0\r";

//const char SONFY []PROGMEM = "www.sonfy.pl\r";
const char SYS []PROGMEM = "***Raport***\r";
const char ALDO []PROGMEM = "Alarm nr: ";
const char BCT []PROGMEM = "Temp: ----\r";
const char R []PROGMEM = "\r";
const char RCLIP_OFF []PROGMEM = "CLP: ON \r";
const char RCLIP_ON []PROGMEM = "CLP: LOCK \r";
const char ALA_ON []PROGMEM = "ARM: ON \r";
const char ALARM_LOCK []PROGMEM = "ARM: LOCK \r";
const char RESET []PROGMEM = "#RESET";



const char OUT1_ON  []PROGMEM = "OUT1: ON\r";
const char OUT1_OFF []PROGMEM = "OUT1: OFF\r";

const char OUT2_ON  []PROGMEM = "OUT2: ON\r";
const char OUT2_OFF []PROGMEM = "OUT2: OFF\r";

const char P_RAPORT_ON[]   PROGMEM = "Raport: ON  \r";    // Włączenie auto-raportowania
const char P_RAPORT_LOCK[] PROGMEM = "Raport: LOCK \r";  // Wyłączenie auto-raportowania



//char eem_buf_1[MOJ_BUFOR] EEMEM;
//char eem_buf_2[MOJ_BUFOR] EEMEM;


char globalna_tablica_1 [15];
char globalna_tablica_2 [15];


// Removed: const char B_ON_TIME [] PROGMEM = "OUT2#ON";

#endif /* TABLICE_H_ */
