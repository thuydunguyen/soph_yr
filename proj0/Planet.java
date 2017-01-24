public class Planet {
public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;
	public double G = 6.67*Math.pow(10, -11);


	public Planet(double xP, double yP, double xV,
              double yV, double m, String img) {

        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;

        }

    public Planet(Planet p) {
    	xxPos = p.xxPos;
    	yyPos = p.yyPos;
    	xxVel = p.xxVel;
    	yyVel = p.yyVel;
    	mass = p.mass;
    	imgFileName = p.imgFileName;
  		}

  	private double xdist(Planet p) {
  		return xxPos - p.xxPos;}

  	private double ydist(Planet p) {
  		return yyPos - p.yyPos;}
  				

  	public double calcDistance(Planet p) {
  		return Math.sqrt((xdist(p) * xdist(p)) + (ydist(p) * ydist(p)));
  	}


  	public double calcForceExertedBy(Planet p) {
  		return (G*mass*p.mass)/(calcDistance(p)*calcDistance(p));}


  	public double calcForceExertedByX(Planet p){
  		return -calcForceExertedBy(p)*xdist(p)/calcDistance(p);}

  	public double calcForceExertedByY(Planet p){
  		return -calcForceExertedBy(p)*ydist(p)/calcDistance(p);}

  	public double calcNetForceExertedByX(Planet[] planets){
  		double t_force = 0;
  		double force;
  		for (int n=0; n<planets.length;n=n+1){
  		if (this.equals(planets[n])) {continue;}
  		t_force += calcForceExertedByX(planets[n]);}
  		return t_force;}

  	public double calcNetForceExertedByY(Planet[] planets){
  		double t_force = 0;
  		double force;
  		for (int n=0; n<planets.length;n=n+1){
  		if (this.equals(planets[n])) {continue;}
  		t_force += calcForceExertedByY(planets[n]);}
  		return t_force;}

  	public void update(double dt, double fX, double fY) {
  		double aX = fX/mass;
  		double aY = fY/mass;
  		xxVel += dt*aX;
  		yyVel += dt*aY;
  		xxPos += xxVel*dt;
  		yyPos += yyVel*dt;
  	} 

  	public void draw() {
  		StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);}


 	}

