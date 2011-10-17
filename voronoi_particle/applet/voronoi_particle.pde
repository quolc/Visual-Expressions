PGraphics pg;
PImage src;

VoronoiGenerator vg;

// constants
int SEEDS = 400;

float simplify = 1.0;
int speedConst = 250;

void setup() {
  size(600, 600);
  
  pg = createGraphics(int(width/simplify), int(height/simplify), P2D);
  pg.stroke(0);
  pg.strokeWeight(10);
  
  src = loadImage("src.png");
}

void draw() {
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
    
    if(vg.dead()) {
      this.vg=null;
      print("reset");
    }
    
    image(pg, 0, 0, width, height);
  }
}

void mouseClicked() {
  if(vg == null) {
    vg = new VoronoiGenerator(int(mouseX/simplify), int(mouseY/simplify), src);
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
  
  public boolean dead() {
    for(int i=0; i<seeds.size(); i++) {
      VoronoiParticle vp = (VoronoiParticle)(this.seeds.get(i));
      if(!vp.dead()) {
        return false;
      }
    }
    return true;
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
  
  public VoronoiParticle(PVector pos, PVector singularity) {
    this.pos = pos;
    float velx = (pos.x-singularity.x) / sqrt(sq(pos.x-singularity.x) + sq(pos.y-singularity.y));
    float vely = (pos.y-singularity.y) / sqrt(sq(pos.x-singularity.x) + sq(pos.y-singularity.y));
    this.vel = new PVector(velx/(abs(pos.x-singularity.x)+100)*speedConst*random(0.5, 1.5), vely/(abs(pos.y-singularity.y)+100)*speedConst*random(0.5, 1.5));
    this.acc = new PVector(0, 0.2);
    this.theta_vel=random(-PI/20, PI/20) / sqrt(sq(pos.x-singularity.x) + sq(pos.y-singularity.y)) * 100;
    
    this.area = new ArrayList();
  }
  
  public void addArea(int x, int y) {
    this.area.add(new PVector(x, y));
  }
  
  public int x() {
    return int(this.pos.x); 
  }
  
  public int y() {
    return int(this.pos.y);
  }
  
  public boolean invalid() {
    if(this.area.size()==0) return true;
    else return false;
  }
  
  public boolean dead() {
    if(this.pos.y > 2*pg.height || this.invalid()) return true;
    else return false;
  }

  public void setFragment() {
    int mx=10000, my=10000, Mx=-1, My=-1;
    for(int i=0; i<this.area.size(); i++) {
      PVector vec = (PVector)(this.area.get(i));
      if(vec.x < mx) mx = int(vec.x);
      if(vec.x > Mx) Mx = int(vec.x);
      if(vec.y < my) my = int(vec.y);
      if(vec.y > My) My = int(vec.y);
    }
    this.particleGraphics = createGraphics(Mx-mx+1, My-my+1, P2D);
    this.particleGraphics.beginDraw();
    this.particleGraphics.loadPixels();
    pg.loadPixels();
    for(int i=0; i<this.area.size(); i++) {
      PVector vec = (PVector)(this.area.get(i));
      this.particleGraphics.pixels[int(vec.x)-mx+(int(vec.y)-my)*this.particleGraphics.width] = pg.pixels[int(vec.x) + int(vec.y)*pg.width];
    }
    this.particleGraphics.updatePixels();
    this.particleGraphics.endDraw();
    
    this.offset = new PVector(int(pos.x)-mx, int(pos.y)-my);
  }
  
  public void drawParticle() {
    pg.pushMatrix();
    pg.translate(pos.x, pos.y);
    pg.rotate(theta);
    pg.image(this.particleGraphics, -int(offset.x), -int(offset.y), this.particleGraphics.width, this.particleGraphics.height);
    pg.popMatrix();
  }
  
  public void proc() {
    this.pos.add(this.vel);
    this.vel.add(this.acc);
    this.theta += this.theta_vel;
  }
}
