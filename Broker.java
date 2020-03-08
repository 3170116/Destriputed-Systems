import java.util.*;

class Broker {
	
    List<Consumer> registeredUsers;
    List<Publisher> registeredPublishers;

    public Publisher acceptConection(Publisher publisher) {
        return publisher;
    }

    public List<Consumer> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<Consumer> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public List<Publisher> getRegisteredPublishers() {
        return registeredPublishers;
    }

    public void setRegisteredPublishers(List<Publisher> registeredPublishers) {
        this.registeredPublishers = registeredPublishers;
    }
    
    public void calculateKeys() {}
    public Consumer acceptConnection(Consumer consumer) {return consumer;}
    public void notifyPublisher(String str){}
    public void pull(ArtistName artname) {}
}
