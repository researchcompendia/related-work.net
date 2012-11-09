package de.renepickhardt.gwt.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Author implements IsSerializable {
	public Author() {
		// TODO Auto-generated constructor stub
	}
	public String name;

	public Author(Author a){
		name=a.name;
	}
	
	public Author(String name) {
		this.name = name;
	}

	public String getHtmlLink(){
		String result = "";
		result = result + "<a href='#author_" + name + "'>" + name + "</a>";
		return result;
	}
	
	public String renderSRP() {
		return getHtmlLink();
	}

}