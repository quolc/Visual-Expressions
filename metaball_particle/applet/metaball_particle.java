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

public class metaball_particle extends PApplet {

ArrayList particleSystems;
PGraphics pg;

int mr, mg, mb;

float simplify = 2;
int far_threshold = 5000;

boolean stop = false;

public void setup() {
  size(600, 600);
  
  pg = createGraphics((int)(width/simplify), (int)(height/simplify), P2D);
  particleSystems = new ArrayList();
}

public void draw() {
  background(0);
  
  // draw metaballs
  pg.beginDraw();
  pg.loadPixels();
  for(int y=0; y<pg.height; y++) {
    for(int x=0; x<pg.width; x++) {
      mr = 1;
      mg = 1;
      mb = 1;
      for(int i=0; i<particleSystems.size(); i++) {
        ParticleSystem ps = (ParticleSystem)this.particleSystems.get(i);
        ps.drawMetaball(x, y);
      }
      pg.pixels[x+y*pg.width] = color(mr, mg, mb);
    }
  }
  pg.updatePixels();
  pg.endDraw();
  
  image(pg, 0, 0, width, height);
  
  // proc
  for(int i=0; i<particleSystems.size(); i++) {
    ParticleSystem ps = (ParticleSystem)this.particleSystems.get(i);
    ps.proc();
    if(ps.dead()) {
      this.particleSystems.remove(i);
      i--;
    }
  }
}

public void mouseClicked() {
  particleSystems.add(new ParticleSystem(new PVector(mouseX/simplify, mouseY/simplify), PApplet.parseInt(random(5, 6))));
}

class ParticleSystem {
  ArrayList particles;
  
  public ParticleSystem(PVector pos, int particleNum) {
    this.particles = new ArrayList();
    
    print(particleNum);
    
    for(int i=0; i<particleNum; i++) {
      PVector newpos = new PVector(pos.x, pos.y);
      float theta = random(PI/6*8, PI/6*10);
      float r = random(2.0f, 3.0f);
      PVector vel = new PVector(r*cos(theta), r*sin(theta));
      this.particles.add(new Particle(newpos, vel, PApplet.parseInt(random(120, 150))));
    }
  }
  
  public void drawMetaball(int x, int y) {
    for(int i=0; i<this.particles.size(); i++) {
      Particle p = ((Particle)this.particles.get(i));
      p.drawMetaball(x, y);
    }
  }
  
  public void proc() {
    for(int i=0; i<this.particles.size(); i++) {
      Particle p = ((Particle)this.particles.get(i));
      p.proc();
      if(p.dead()) {
        this.particles.remove(i);
        i--;
      }
    }
  }
  
  public boolean dead() {
    for(int i=0; i<this.particles.size(); i++) {
      if(!((Particle)this.particles.get(i)).dead()) {
        return false;
      }
    }
    return true;
  }
}

class Particle {
  PVector pos;
  PVector vel;
  PVector acc;
  int t;
  int life;
  float r_ratio = 1, g_ratio = 1, b_ratio = 1;
  
  public Particle(PVector initPos, PVector initVel, int life) {
    this.pos = initPos;
    this.vel = initVel;
    this.life = life;
    
    this.acc = new PVector(0, 0.1f);
    
    this.r_ratio = random(0, 1);
    this.g_ratio = random(0, 1);
    this.b_ratio = random(0, 1);
  }
  
  public void drawMetaball(int x, int y) {
    if((x-pos.x)*(x-pos.x) + (y-pos.y)*(y-pos.y) > far_threshold) return;
    float p = 20000/((x-pos.x)*(x-pos.x) + (y-pos.y)*(y-pos.y) + 1);
    mr += p * r_ratio;
    mg += p * g_ratio;
    mb += p * b_ratio;
  }
  
  public void proc() {
    this.vel.add(this.acc);
    this.pos.add(this.vel);
    
    t++;
  }
  
  public boolean dead() {
    if(t>life) return true;
    else return false;
  }
  
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "metaball_particle" });
  }
}
