int X=400;
int Y=400;

color[][] previous = new color[X][Y];
color[][] next = new color[X][Y];

void setup() {
  size(X, Y);
  randomSeed(0);
  colorMode(HSB, 100);
  
  for(int i=0; i<X; i++) {
    for(int j=0; j<Y; j++) {
      previous[i][j] = color(0,0,0);
      next[i][j] = color(0,0,0);
    }
  }
}

void draw() {
  generateSeed();
  firing();
  
  loadPixels();
  for(int i=0; i<X; i++) {
    for(int j=0; j<Y; j++) {
      pixels[i+j*X] = next[i][j];
    }
  }
  updatePixels();
  
  for(int i=0; i<X; i++) {
    for(int j=0; j<Y; j++) {
      previous[i][j] = next[i][j];
    }
  }
}

void generateSeed() {
  for(int i=0; i<width; i++) {
    int h = int(random(20));
    int s = int(random(50, 100));
    int b = int(random(20, 100));
    next[i][Y-1] = color(h, s, b);
  }
}

void firing() {
  for(int i=0; i<height-1; i++) {
    for(int j=0; j<width; j++) {
      float h=0, s=0, b=0;
      int n=0;
      if(i+3<Y) {
        h += hue(previous[j][i+3]);
        s += saturation(previous[j][i+3]);
        b += brightness(previous[j][i+3]);
        n++;
      }
      if(i+2<Y) {
        h += hue(previous[j][i+2]);
        s += saturation(previous[j][i+2]);
        b += brightness(previous[j][i+2]);
        n++;
      }
      if(j-1>=0) {
        h += hue(previous[j-1][i+1]);
        s += saturation(previous[j-1][i+1]);
        b += brightness(previous[j-1][i+1]);
        n++;
      }
      if(j+1<X) {
        h += hue(previous[j+1][i+1]);
        s += saturation(previous[j+1][i+1]);
        b += brightness(previous[j+1][i+1]);
        n++;
      }
      h += hue(previous[j][i+1])*2;
      s += saturation(previous[j][i+1])*2;
      b += brightness(previous[j][i+1])*2;
      n+=2;
      
      h/=n;
      s/=n;
      b/=(n+0.1);
      color newColor = color(int(h), int(s), int(b));
      next[j][i] = newColor;
    }
  }
}
