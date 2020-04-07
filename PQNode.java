
public class PQNode<P,T> {
public T data;
public P priority;
public PQNode<P,T> next;
public PQNode() {
	data = null;
	priority = null;
	next = null;
}
public PQNode(P p,T d){
	data = d;
	priority = p;
	next = null;
}
}
