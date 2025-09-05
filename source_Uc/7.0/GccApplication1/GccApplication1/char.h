// char.h
/*
 * char.h
 *
 * Created on: 10.03.2017
 * Author: Robert
 */

#ifndef CHAR_H_
#define CHAR_H_



volatile uint32_t Timer1, Timer2, Timer3, Timer4, Timer5, Timer6;  // Declaration only
//volatile  uint32_t Timer1, Timer2, Timer3, Timer4, Timer6, Timer5; Timer6 = 0;
uint8_t czujniki;
uint8_t subzero, cel, cel_fract_bits;
uint8_t odczyt;
uint8_t znak;

uint8_t key_lock;


// The following line for Timer6 is redundant and was causing re-declaration, so it's removed.
// volatile uint32_t Timer6;  // musi być uint32_t dla dużych wartości
#endif /* CHAR_H_ */
