package main;

import model.ServerRequestModel;

public class ServerMain {

	public static void main(String[] args) throws Exception {		
		
		ServerRequestModel s1 = new ServerRequestModel(24,115,0);
		
		ServerRequestModel s2 = new ServerRequestModel(8,0,29);
		
		ServerRequestModel s3 = new ServerRequestModel(7,180,195);
		
		//s1.getCosts();
		
		//s2.getCosts();
		
		s3.getCosts();
		
	}

}
