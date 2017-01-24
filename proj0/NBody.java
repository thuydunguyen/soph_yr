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





}
