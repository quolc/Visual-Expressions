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

public class voronoi_particle extends PApplet {

PGraphics pg;
PImage src;

VoronoiGenerator vg;

// constants
int SEEDS = 400;

float simplify = 1.5f;
int speedConst = 250;

public void setup() {
  size(600, 600);
  
  pg = createGraphics(PApplet.parseInt(width/simplify), PApplet.parseInt(height/simplify), P2D);
  pg.stroke(0);
  pg.strokeWeight(10);
  
  src = loadImage("src.png");
}

public void draw() {
  background(255);
    
  if(this.vg == null) {
    pg.beginDraw();
    pg.loadPixels();
    for(int i=0; i<pg.width; i++) {
      for(int j=0; j<pg.height; j++) {
        pg.pixels[j*pg.width + i] = color(0);
      } 
    }
    pg.updatePixels();
    pg.image(src, 0, 0, pg.width, pg.height);
    pg.endDraw();
    
    image(pg, 0, 0, width, height);
  } else {
    pg.beginDraw();
    pg.loadPixels();
    for(int i=0; i<pg.width; i++) {
      for(int j=0; j<pg.height; j++) {
        pg.pixels[j*pg.width + i] = color(0);
      } 
    }
    vg.drawVoronoi();
    pg.updatePixels();
    pg.endDraw();
    
    vg.proc();
    
    image(pg, 0, 0, width, height);
  }
}

public void mouseClicked() {
  if(vg == null) {
    vg = new VoronoiGenerator(PApplet.parseInt(mouseX/simplify), PApplet.parseInt(mouseY/simplify), src);
  }
}

class VoronoiGenerator {
  ArrayList seeds;
  
  public VoronoiGenerator(int x, int y, PImage source) {
    this.seeds = new ArrayList();
    
    // generate seeds
    for(int i=0; i<SEEDS; i++) {
      float r = random(0, max(max(x, pg.width-x), max(y, pg.width-y)));
      float theta = random(0, PI*2);
      
      PVector pos = new PVector(x + r*cos(theta), y + r*sin(theta));
      if(pos.x < 0 || pg.width <= pos.x || pos.y < 0 || pg.height <= pos.y) {
        i--;
        continue;
      } else {
        seeds.add(new VoronoiParticle(pos, new PVector(x, y)));
      }
    }
    
    // generate voronoi diagram
    for(int i=0; i<pg.width; i++) {
      for(int j=0; j<pg.height; j++) {
        VoronoiParticle minp = (VoronoiParticle)(seeds.get(0));
        for(int k=1; k<seeds.size(); k++) {
          VoronoiParticle vp = (VoronoiParticle)(seeds.get(k));
          if(sq(vp.x()-i) + sq(vp.y()-j) < sq(minp.x()-i) + sq(minp.y()-j)) {
            minp = vp; 
          }
        }
        minp.addArea(i, j);
      }
    }
    
    // generate fragments
    for(int i=0; i<seeds.size(); i++) {
      VoronoiParticle vp = (VoronoiParticle)(seeds.get(i));
      if(!vp.invalid()) {
        vp.setFragment();
      }
    }
  }
  
  public void drawVoronoi() {
    for(int i=0; i<seeds.size(); i++) {
      VoronoiParticle vp = (VoronoiParticle)(this.seeds.get(i));
      if(!vp.invalid()) {
        vp.drawParticle();
      }
    }
  }
  
  public void proc() {
    for(int i=0; i<seeds.size(); i++) {
      VoronoiParticle vp = (VoronoiParticle)(this.seeds.get(i));
      if(!vp.invalid()) {
        vp.proc();
      }
    }
  }
}

class VoronoiParticle {
  PVector pos;
  PVector vel;
  PVector acc;
  float theta=0;
  float theta_vel;
  
  PVector offset;
  PGraphics particleGraphics;
  ArrayList area;
  
  public boolean invalid() {
    if(this.area.size()==0) return true;
    else return false;
  }
  
  public VoronoiParticle(PVector pos, PVector singularity) {
    this.pos = pos;
    float velx = (pos.x-singularity.x) / sqrt(sq(pos.x-singularity.x) + sq(pos.y-singularity.y));
    float vely = (pos.y-singularity.y) / sqrt(sq(pos.x-singularity.x) + sq(pos.y-singularity.y));
    this.vel = new PVector(velx/(abs(pos.x-singularity.x)+100)*speedConst*random(0.5f, 1.5f), vely/(abs(pos.y-singularity.y)+100)*speedConst*random(0.5f, 1.5f));
    this.acc = new PVector(0, 0.2f);
    this.theta_vel=random(-PI/20, PI/20);
    
    this.area = new ArrayList();
  }
  
  public void addArea(int x, int y) {
    this.area.add(new PVector(x, y));
  }
  
  public int x() {
    return PApplet.parseInt(this.pos.x); 
  }
  
  public int y() {
    return PApplet.parseInt(this.pos.y);
  }

  public void setFragment() {
    int mx=10000, my=10000, Mx=-1, My=-1;
    for(int i=0; i<this.area.size(); i++) {
      PVector vec = (PVector)(this.area.get(i));
      if(vec.x < mx) mx = PApplet.parseInt(vec.x);
      if(vec.x > Mx) Mx = PApplet.parseInt(vec.x);
      if(vec.y < my) my = PApplet.parseInt(vec.y);
      if(vec.y > My) My = PApplet.parseInt(vec.y);
    }
    println((Mx-mx+1)+" "+(My-my+1));
    this.particleGraphics = createGraphics(Mx-mx+1, My-my+1, P2D);
    this.particleGraphics.beginDraw();
    this.particleGraphics.loadPixels();
    pg.loadPixels();
    for(int i=0; i<this.area.size(); i++) {
      PVector vec = (PVector)(this.area.get(i));
      this.particleGraphics.pixels[PApplet.parseInt(vec.x)-mx+(PApplet.parseInt(vec.y)-my)*this.particleGraphics.width] = pg.pixels[PApplet.parseInt(vec.x) + PApplet.parseInt(vec.y)*pg.width];
    }
    this.particleGraphics.updatePixels();
    this.particleGraphics.endDraw();
    
    this.offset = new PVector(PApplet.parseInt(pos.x)-mx, PApplet.parseInt(pos.y)-my);
  }
  
  public void drawParticle() {
    pg.pushMatrix();
    pg.translate(pos.x, pos.y);
    pg.rotate(theta);
    pg.image(this.particleGraphics, -PApplet.parseInt(offset.x), -PApplet.parseInt(offset.y), this.particleGraphics.width, this.particleGraphics.height);
    pg.popMatrix();
  }
  
  public void proc() {
    this.pos.add(this.vel);
    this.vel.add(this.acc);
    this.theta += this.theta_vel;
  }
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "voronoi_particle" });
  }
}
