import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class retina extends PApplet {

float[][] colorTable = {
{0.03215f,0.00000f,0.06704f}, // 380nm
{0.04994f,0.00000f,0.11193f},
{0.08777f,0.00000f,0.19463f},
{0.14477f,0.00000f,0.32333f},
{0.23877f,0.00000f,0.54172f},
{0.32410f,0.00000f,0.76634f},
{0.33021f,0.00000f,0.85104f},
{0.27034f,0.00000f,0.85575f},
{0.06765f,0.00000f,0.83146f},
{0.00000f,0.09932f,0.73634f},
{0.00000f,0.31041f,0.59180f},
{0.00000f,0.42097f,0.44733f},
{0.00000f,0.52628f,0.32488f},
{0.00000f,0.64040f,0.18917f},
{0.00000f,0.73454f,0.00000f},
{0.00000f,0.78022f,0.00000f},
{0.00000f,0.79118f,0.00000f},
{0.00000f,0.77451f,0.00000f},
{0.39971f,0.73284f,0.00000f},
{0.64746f,0.66278f,0.00000f},
{0.81826f,0.56132f,0.00000f},
{0.93567f,0.42412f,0.00000f},
{0.99758f,0.23823f,0.00000f},
{1.00000f,0.00000f,0.00000f},
{0.94530f,0.00000f,0.00000f},
{0.83838f,0.00000f,0.00000f},
{0.71600f,0.00000f,0.00000f},
{0.58376f,0.00000f,0.00000f},
{0.45727f,0.00000f,0.00000f},
{0.34301f,0.00000f,0.00000f},
{0.25843f,0.00000f,0.00000f},
{0.18611f,0.00000f,0.00000f},
{0.13614f,0.00000f,0.00000f},
{0.10007f,0.00000f,0.00000f},
{0.07337f,0.00000f,0.00000f},
{0.05252f,0.00000f,0.00000f},
{0.03756f,0.00000f,0.00000f},
{0.02624f,0.00000f,0.00000f},
{0.02081f,0.00000f,0.00000f},
{0.01729f,0.00000f,0.00264f},
{0.00000f,0.00000f,0.00000f}};

// range = 0 - 100
// 420(blue) / 534(green) / 564(red)
float[][] retinaTable = {
  {70, 35, 40}, // 380
  {78, 37, 39},
  {89, 38, 39}, // 400
  {97, 38, 36},
  {100, 37, 34},
  {95, 35, 31},
  {88, 36, 30},
  {71, 38, 28},
  {51, 42, 30},
  {38, 49, 34},
  {26, 57, 40},
  {20, 68, 48},
  {15, 79, 56}, // 500
  {10, 87, 64},
  {7, 95, 74},
  {3, 100, 83},
  {0, 99, 92},
  {0, 94, 97},
  {0, 83, 100},
  {0, 69, 98},
  {0, 53, 95},
  {0, 41, 86},
  {0, 30, 75}, // 600
  {0, 21, 62},
  {0, 14, 48},
  {0, 9, 33},
  {0, 7, 27},
  {0, 3, 17},
  {0, 0, 10},
  {0, 0, 7}, 
  {0, 0, 5}, // 680
};

public void setup() {
  size(800, 800, OPENGL);
  smooth();
  
  colorMode(RGB, 100);
}

public void draw() {
  camera(50 + 200*cos((float)frameCount / 180), 50 + 200*sin((float)frameCount / 180), 200,
  50, 50, 50, 0, 0, -1);

  background(100);

  noFill();
  strokeWeight(2);
  stroke(0, 0, 50);
  line(0, 0, 0, 150, 0, 0);
  stroke(0, 50, 0);
  line(0, 0, 0, 0, 150, 0);
  stroke(50, 0, 0);
  line(0, 0, 0, 0, 0, 150);
//  translate(50, 50, 50);
//  box(100);
  
//  translate(-50, -50, -50);
  
  int i=0;
  for (int l=380; l<=680; l+=10) {
    pushMatrix();

    stroke(colorTable[i][0]*100, colorTable[i][1]*100, colorTable[i][2]*100);
    translate(retinaTable[i][0], retinaTable[i][1], retinaTable[i][2]);
    sphere(3);
    i++;

    popMatrix();
  }
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "retina" });
  }
}
