import java.util.*;;

public class Broker {
	
	List<Consumer> registeredUsers;
	List<Publisher> registeredPublishers;
	
	public void calculateKeys() {}
	public Publisher acceptConection(Publisher publisher) {return publisher;}
	public Consumer acceptConnection(Consumer consumer) {return consumer;}
	public void notifyPublisher(String str){}
	public void pull(ArtistName artname) {}
}
