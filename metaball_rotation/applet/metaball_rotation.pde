int ball_num = 12;

int min_r[] = {50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0};
int max_r[] = {180, 180, 180, 180, 180, 180, 120, 120, 120, 120, 120, 120};

float theta[] = {0, PI/3, PI/3*2, PI, PI/3*4, PI/3*5, PI/6, PI/2, PI/6*5, PI/6*7, PI/2*3, PI/6*11};
float r_theta[] = {0, 0, 0, 0, 0, 0, PI, PI, PI, PI, PI, PI};

float r_ratio[] = {0, 0, 0, 0, 0, 0, 0.5, 1, 0.5, 1, 0.5, 1};
float g_ratio[] = {1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1};
float b_ratio[] = {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0, 0, 0, 0, 0, 0};

Point2D position[];

float v_theta = PI/60;
float v_r_theta = PI/60;

PGraphics pg;
int[][] vx, vy;

class Point2D {
  public int x;
  public int y;
  public Point2D(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

Point2D calcPos(int num) {
  float x = pg.width/2 + (min_r[num] + (max_r[num]-min_r[num]) * (sin(r_theta[num]) + 1)/2) * cos(theta[num]) / 2;
  float y = pg.height/2 + (min_r[num] + (max_r[num]-min_r[num]) * (sin(r_theta[num]) + 1)/2) * sin(theta[num]) / 2;
  return new Point2D(int(x), int(y));
}

void setup() {
  size(500, 500);
  pg = createGraphics(width/2, height/2, P2D);
  vx = new int[ball_num][width];
  vy = new int[ball_num][height];
  position = new Point2D[ball_num];
  colorMode(RGB, 255);
}

void draw() {
  background(0);
  
  stroke(255);
  strokeWeight(2);
  
  // calculate each position
  for(int i=0; i<ball_num; i++) {
    position[i] = calcPos(i);
  }
  
  // vx/vy
  for(int i=0; i<ball_num; i++) {
    for(int x=0; x<pg.width; x++) {
      vx[i][x] = int(sq(position[i].x - x));
    }
    for(int y=0; y<pg.height; y++) {
      vy[i][y] = int(sq(position[i].y - y));
    }
  }
  
  pg.beginDraw();
  pg.loadPixels();
  for(int y=0; y<pg.height; y++) {
    for(int x=0; x<pg.width; x++) {
      int mr = 1, mg = 1, mb = 1;
      for(int i=0; i<ball_num; i++) {
        float p = 30000/(vy[i][y] + vx[i][x] + 1);
        mr += p * r_ratio[i];
        mg += p * g_ratio[i];
        mb += p * b_ratio[i];
      }
      pg.pixels[x+y*pg.width] = color(mr, mg, mb);
    } 
  }
  pg.updatePixels();
  pg.endDraw();
  
  image(pg, 0, 0, width, height);
  
  // turn 
  for(int i=0; i<ball_num; i++) {
    theta[i] += v_theta;
    theta[i] %= (2*PI);
    r_theta[i] += v_r_theta;
    r_theta[i] %= (2*PI);
    
    r_ratio[7] = (sin(theta[7])+1)/3;
    g_ratio[7] = (sin(theta[7] + PI/3*2)+1)/3;
    b_ratio[7] = (sin(theta[7] + PI/3*4)+1)/3;
    
    
    r_ratio[9] = (sin(theta[9])+1)/3;
    g_ratio[9] = (sin(theta[9] + PI/3*2)+1)/3;
    b_ratio[9] = (sin(theta[9] + PI/3*4)+1)/3;
    
    
    r_ratio[11] = (sin(theta[11])+1)/3;
    g_ratio[11] = (sin(theta[11] + PI/3*2)+1)/3;
    b_ratio[11] = (sin(theta[11] + PI/3*4)+1)/3;
  }
}
