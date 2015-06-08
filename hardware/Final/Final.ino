#include "ClickButton.h"

// the Button
const int buttonPin1 = 2;
long cmd = 0;
ClickButton button1(buttonPin1, HIGH, CLICKBTN_PULLUP);

void setup()
{
  button1.debounceTime   = 20;   // Debounce timer in ms
  button1.multiclickTime = 250;  // Time limit for multi clicks
  button1.longClickTime  = 500; // time until "held-down clicks" register
  Serial.begin(9600);
}


void loop()
{
  // Update button state
  button1.Update();  
  // Simply toggle LED on single clicks
  // (Cant use LEDfunction like the others here,
  //  as it would toggle on and off all the time)
  if(button1.clicks < 0)
    //Serial.print("-1");
    Bean.setScratchNumber(1, -1);
    //cmd = -1;
  if(button1.clicks == 1) 
    //Serial.print("1");
    Bean.setScratchNumber(1, 1);
    //cmd = 1;
  if(button1.clicks == 2)
    //Serial.print("2");
    Bean.setScratchNumber(1, 2);
    //cmd = 2;
  if(button1.clicks == 3)
    //Serial.print("3");
    Bean.setScratchNumber(1, 3);
    //cmd = 3;
  /*if(cmd != 0)
  {
    Bean.setScratchNumber(1, cmd);
    //Serial.print(cmd);
    //Bean.sleep(1000);
    //cmd = Bean.readScratchNumber(1);
    //Bean.sleep(1000);
    //Serial.println(cmd);
  }*/
}
