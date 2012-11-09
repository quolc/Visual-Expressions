Section main;

void setup() {
  main = new Section(null);
  size(800, 600);
}

void draw() {
  main.draw();
}

class Section {
  PGraphics pg;
  Section parent;
  int n;
  boolean direction;
  Section[] children;
  
  public Section(Section parent) {
    this.parent = parent;
    this.n = int(random(1, 4));
    this.direction = random(0, 1) < 0.5;
    this.children = new Section[n];
  }
  
  public PImage draw() {
    for(int i=0; i<n; i++) {
      if(direction) {
        
      } else {
        
      }
    }
  }
}
