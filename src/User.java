import java.util.Objects;

public abstract class User implements Observer {
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected Language language;
    public User(String id, String firstName, String lastName,
                String email, String password, Language language){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.language = language;
    }
    public boolean login(){
        return true;
    };
    public void logout(){

    };
    @Override
    public String toString(){
        return firstName + " " + lastName + " has id " + id + " email " + email;
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
    @Override
    public void update(ResearchPaper paper){
        if (paper != null) {
            System.out.println("[NOTIFICATION] " + this.firstName + " " + this.lastName +
                    " has been notified about new paper: " + paper.getTitle());
        }
    }
    public String getId(){
        return id;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
    public Language getLanguage(){
        return language;
    }
}
