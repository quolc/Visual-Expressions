Particle[] particles;
int N=10;
float K=0.002;
float pathprob=0.5;

void setup() {
   size(400, 400, JAVA2D);

   particles = new Particle[N];
   for(int i=0; i<N; i++) {
     float x = random(0, width+1);
     float y = random(0, height+1);
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
   
   ellipseMode(CENTER);
   strokeWeight(1);
   smooth();
}

void draw() {
  background(191);
  for(int i=0; i<N; i++) {
    // draw line
    for(int j=0; j<particles[i].connects; j++) {
      int k = particles[i].connections[j];
      if(i<k) {
        line(int(particles[i].pos.x), int(particles[i].pos.y), int(particles[k].pos.x), int(particles[k].pos.y));
      }
    }
    
    // draw particle
    ellipse(int(particles[i].pos.x), int(particles[i].pos.y), 20, 20);
  }
  
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
   
   public Particle(float x, float y) {
     this.pos = new PVector(x, y);
     this.vel = new PVector(0, 0);
     this.acc = new PVector(0, 0);
     this.newpos = new PVector(0, 0);
     this.connections = new int[N];
     this.connects=0;
   }
   
   public void simulate() {
     this.newpos.x = this.pos.x;
     this.newpos.y = this.pos.y;
     this.acc.x = 0;
     this.acc.y = 0;
     for(int i=0; i<connects; i++) {
       PVector vec = new PVector(particles[this.connections[i]].pos.x - this.pos.x, 
                                 particles[this.connections[i]].pos.y - this.pos.y);
       vec.mult(K);
       this.acc.add(vec);
     }
     this.vel.add(this.acc);
     this.newpos.add(this.vel);
   }
   
   public void apply() {
     this.pos.x = this.newpos.x;
     this.pos.y = this.newpos.y;
   }
   
   public void addConnection(int number) {
     this.connections[this.connects++] = number; 
   }
}
