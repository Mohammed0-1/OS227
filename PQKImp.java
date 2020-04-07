public class PQKImp<P extends Comparable<P>, T> {
	private PQNode<P,T> head;
	private int size;
	public PQKImp() {
		head =null;
		size = 0;
	}
	public int length() {
		return size;
	}
	public PQNode<P,T> peek(){
		if(size>0)
		return head;
		return null;
	}

	// Enqueue a new element. The queue keeps the k elements with the highest priority. In case of a tie apply FIFO.
	public void enqueue(P pr, T e) {
		PQNode<P,T> tmp = new PQNode<P,T>(pr, e);
		if((size == 0) || (pr.compareTo(head.priority)>0)) {
			tmp.next = head;
			head = tmp;
		}
		else {
			PQNode<P,T> p = head;
			PQNode<P,T> q = null;
			while((p != null) && (pr.compareTo(p.priority)<=0)) {
				q = p;
				p = p.next;
			}
			tmp.next = p;
			q.next = tmp;
		}
		size++;
	}

	// Serve the element with the highest priority. In case of a tie apply FIFO.
	public T serve(){
		if(length() == 0)
			return null;
		PQNode<P,T> temp = head;
		head = head.next;
		size--;
		return temp.data;
	}
}
