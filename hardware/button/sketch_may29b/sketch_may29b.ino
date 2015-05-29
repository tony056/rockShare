#include "ClickButton.h"

// the Button
const int buttonPin1 = 2;
ClickButton button1(buttonPin1, HIGH, CLICKBTN_PULLUP);

void setup()
{
  // Setup button timers (all in milliseconds / ms)
  // (These are default if not set, but changeable for convenience)
  button1.debounceTime   = 20;   // Debounce timer in ms
  button1.multiclickTime = 250;  // Time limit for multi clicks
  button1.longClickTime  = 500; // time until "held-down clicks" register
}


void loop()
{
  // Update button state
  button1.Update();  

  // Simply toggle LED on single clicks
  // (Cant use LEDfunction like the others here,
  //  as it would toggle on and off all the time)
  if(button1.clicks < 0)
    Serial.println("Long Click");
  if(button1.clicks == 1) 
    Serial.println("Single Click");
  if(button1.clicks == 2)
    Serial.println("Double Click");
  if(button1.clicks == 3)
    Serial.println("Triple Click");
}
