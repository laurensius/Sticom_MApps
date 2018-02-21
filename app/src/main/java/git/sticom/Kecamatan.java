package git.sticom;

public class Kecamatan {
	
	private int id;
	private String name;
	
	public Kecamatan(){}
	
	public Kecamatan(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}

}
