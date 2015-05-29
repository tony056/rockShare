#include <CapacitiveSensor.h>

/*
 * CapitiveSense Library Demo Sketch
 * Paul Badger 2008
 * Uses a high value resistor e.g. 10M between send pin and receive pin
 * Resistor effects sensitivity, experiment with values, 50K - 50M. Larger resistor values yield larger sensor values.
 * Receive pin is the sensor pin - try different amounts of foil/metal on this pin
 */

//first var is sender pin, second var is receiver pin
CapacitiveSensor   cs_4_2 = CapacitiveSensor(5,3);        // 10M resistor between pins 4 & 2, pin 2 is sensor pin, add a wire and or foil if desired 

void setup()                    
{
   cs_4_2.set_CS_AutocaL_Millis(0xFFFFFFFF);     // turn off autocalibrate on channel 1 - just as an example
   Serial.begin(9600);
}

void loop()                    
{
    boolean single_tap = false;
    boolean double_tap = false;
    
    long start = millis();
    long window;
    long total1 =  cs_4_2.capacitiveSensor(50);

    Serial.print(millis() - start);        // check on performance in milliseconds
    Serial.print("\t");                    // tab character for debug windown spacing

    Serial.print(total1);                  // print sensor output 1
    Serial.print("\n");

    if(total1 > 100)
    {
        Serial.print("Single tap detected\n");
         /*if(double_tap == true)
           double_tap = !double_tap;
         else
         {
           Serial.print("Single tap detected\n");
           single_tap = true;
           window = millis();
           while( (millis() - window) < 75 )
           {
               total1 =  cs_4_2.capacitiveSensor(100);
               if(total1 > 10)
               {
                   Serial.print("Double tap detected\n");
                   double_tap = true;
                   single_tap = false;
                   break;
               }
           }
         }*/
    }
    
    delay(10);                             // arbitrary delay to limit data to serial port 
}
