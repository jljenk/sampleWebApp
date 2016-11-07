package sample.object.user;


public interface IUserRepository {

  LoginResponse authenticate(User user, String remoteHost);
  String valid(String token);
  void create(User user);

}
