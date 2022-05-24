module com.example {
	// opens com.example to spring.core, spring.beans, spring.context;
	opens com.example;
	requires spring.jcl;
	requires spring.core;
	requires spring.beans;
	requires spring.context;
}
