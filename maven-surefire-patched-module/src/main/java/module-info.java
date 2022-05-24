module com.example {
	exports com.example;
	opens com.example;
	requires spring.jcl;
	requires spring.core;
	requires spring.context;
	requires spring.beans;
}
