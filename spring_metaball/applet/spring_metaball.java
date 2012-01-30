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

public class spring_metaball extends PApplet {

Particle[] particles;
int N=20;
float K=0.002f;
float pathprob=0.5f;

// metaball variables
PGraphics pg;
int mr[][], mg[][], mb[][];
int meta_const = 10000;
float divisor = 1.5f;
int shut_r = 80;

float simplify = 2.0f;
int far_threshold = 500;

public void keyPressed() {
  setup();
}

public void setup() {
  size(600, 600, P2D);

  particles = new Particle[N];
  for(int i=0; i<N; i++) {
    float x = random(0, (width+1)/simplify);
    float y = random(0, (height+1)/simplify);
    particles[i] = new Particle(x, y);
  }
  for(int i=0; i<N; i++) {
    for(int j=i+1; j<N; j++) {
      if(random(0, 1) < pathprob) {
        particles[i].addConnection(j);
        particles[j].addConnection(i); 
      }
    }  
  }
 
  pg = createGraphics((int)(width/simplify), (int)(height/simplify), P2D);
  mr = new int[(int)(width/simplify)][(int)(height/simplify)];
  mg = new int[(int)(width/simplify)][(int)(height/simplify)];
  mb = new int[(int)(width/simplify)][(int)(height/simplify)];
}

public void draw() {
  // draw metaballs
  for(int i=0; i<N; i++) {
    Particle p = this.particles[i];
    int px = (int)p.pos.x;
    int py = (int)p.pos.y;
    for(int y=(py-shut_r >= 0) ? py-shut_r : 0; y<py+shut_r && y<pg.height; y++) {
      for(int x=(px-shut_r >= 0) ? px-shut_r : 0; x<px+shut_r && x<pg.width; x++) {
        p.drawMetaball(x, y);
      }
    }
  }
  pg.beginDraw();
  pg.loadPixels();
  for(int y=0; y<pg.height; y++) {
    for(int x=0; x<pg.width; x++) {
      pg.pixels[x+y*pg.width] = color(mr[x][y], mg[x][y], mb[x][y], 191);
//      mr[x][y] = int(sqrt(mr[x][y]))*4;
//      mg[x][y] = int(sqrt(mg[x][y]))*4;
//      mb[x][y] = int(sqrt(mb[x][y]))*4;
      mr[x][y] /= divisor;
      mg[x][y] /= divisor;
      mb[x][y] /= divisor;
    }
  }
  pg.updatePixels();
  pg.endDraw();
  
  image(pg, 0, 0, width, height);
  
  // simulation
  for(int i=0; i<N; i++) {
    particles[i].simulate();
  }
  
  // apply
  for(int i=0; i<N; i++) {
    particles[i].apply(); 
  }
}


class Particle {
  int[] connections;
  int connects;
  PVector pos;
  PVector newpos;
  PVector vel;
  PVector acc;
   
  float ratio = 1, r_ratio = 1, g_ratio = 1, b_ratio = 1;
   
  public Particle(float x, float y) {
    this.pos = new PVector(x, y);
    this.vel = new PVector(0, 0);
    this.acc = new PVector(0, 0);
    this.newpos = new PVector(0, 0);
    this.connections = new int[N];
    this.connects=0;
    
    this.r_ratio = random(0.5f, 1);
    this.g_ratio = random(0.5f, 1);
    this.b_ratio = random(0.5f, 1);
    this.ratio = 1;
//    this.ratio = random(0.5, 1.0);
  }
   
  public void simulate() {
    this.newpos.x = this.pos.x;
    this.newpos.y = this.pos.y;
    this.acc.x = 0;
    this.acc.y = 0;
    for(int i=0; i<connects; i++) {
      PVector vec = new PVector(particles[this.connections[i]].pos.x - this.pos.x, 
                                particles[this.connections[i]].pos.y - this.pos.y);
      float r = particles[this.connections[i]].pos.dist(this.pos);
      vec.normalize();
      vec.mult(r);
      vec.mult(K);
      this.acc.add(vec);
      }
    this.vel.add(this.acc);
    this.newpos.add(this.vel);
    
    this.ratio = this.acc.mag()/500*255;
  }
   
  public void apply() {
    this.pos.x = this.newpos.x;
    this.pos.y = this.newpos.y;
  }
   
  public void addConnection(int number) {
    this.connections[this.connects++] = number; 
  }
   
  public void drawMetaball(int x, int y) {
    float p = meta_const/((x-pos.x)*(x-pos.x) + (y-pos.y)*(y-pos.y) + 1);
    mr[x][y] += p * r_ratio * ratio;
    mg[x][y] += p * g_ratio * ratio;
    mb[x][y] += p * b_ratio * ratio;
  }
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "spring_metaball" });
  }
}
