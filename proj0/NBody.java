public class NBody {


	public static double readRadius(String args) {

		In in = new In(args);
		int num_pl = in.readInt();
		double radius = in.readDouble();
		return radius;}

	public static Planet[] readPlanets(String args) {
		In in = new In(args);
		int num_pl = in.readInt();
		double radius = in.readDouble();
		Planet[] list = new Planet[num_pl];
		for (int n=0; n < num_pl; n+=1) {
				double xP = in.readDouble();
				double yP = in.readDouble();
				double xV = in.readDouble();
				double yV = in.readDouble();
				double mass = in.readDouble();
				String imgfile = in.readString();
				Planet pl = new Planet(xP, yP, xV, yV, mass, imgfile);
				list[n] = pl;}
		return list;}


	public static void main(String[] args) {
		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String filename = args[2];
		double radius = readRadius(filename);
		Planet[] planets = readPlanets(filename);
		StdDraw.setScale(-radius, radius);
		StdDraw.clear();
		StdDraw.picture(0,0,"images/starfield.jpg");
		for (int n=0; n < planets.length; n+=1) {
			planets[n].draw();
		}

		int num_pl = planets.length;
		for (int time = 0; time < T; time += dt) {
			double[] xForces = new double[num_pl];
			double[] yForces = new double[num_pl];
			for (int i=0; i < num_pl; i+= 1) {
				xForces[i] = planets[i].calcNetForceExertedByX(planets);
				yForces[i] = planets[i].calcNetForceExertedByY(planets);
				}
			for (int j = 0; j < num_pl; j+= 1) {
				planets[j].update(dt, xForces[j], yForces[j]);
				}

			StdDraw.picture(0,0,"images/starfield.jpg");
			for (int n=0; n < planets.length; n+=1) {
			planets[n].draw();
				}
			StdDraw.show(10);
			}

		StdOut.printf("%d\n", planets.length);
		StdOut.printf("%.2e\n", radius);
		for (int i = 0; i < planets.length; i++) {
		StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   		planets[i].xxPos, planets[i].yyPos, planets[i].xxVel, planets[i].yyVel, planets[i].mass, planets[i].imgFileName);	
}		

	}


}
