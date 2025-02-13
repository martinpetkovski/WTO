package wto.repository;

import java.util.Date;
import java.util.List;

import wto.model.Image;

public interface ImageRepository {
	public int create(Image entity);
	public Image read(Integer primaryKey);
	public void update(Image entity);
	public void delete(Integer primaryKey);
	public Image readByAddress(String address);
	public List<String> readNextPrev(Date id, int points, String order);
	public List<Image> readByUserId(int userId);
	public List<Image> readByFollowers(int userid, int page);
	public List<Image> readAll(int selectionFlag, String criteria, String order, int page);
	public List<Image> readByQuery(String query, String order, int page);
	public List<Image> readByTag(String query, String order, int page);
	public String randomImage();
	public List<Image> readByUsername(String username, String order, int page);
	public int numberOfImages(int selectionFlag, String criteria);
	public boolean getToken(String uid);
	List<Image> readByAll(String query, String order, int page);
}
