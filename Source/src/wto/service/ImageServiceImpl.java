package wto.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wto.model.CommentVote;
import wto.model.Follower;
import wto.model.Image;
import wto.model.ImageVote;
import wto.model.Tag;
import wto.repository.CommentVoteRepository;
import wto.repository.FollowerRepository;
import wto.repository.ImageRepository;
import wto.repository.ImageVoteRepository;
import wto.repository.TagRepository;

@Service("ImageService")
public class ImageServiceImpl implements ImageService {

	@Autowired
	ImageRepository img;
	@Autowired
	ImageVoteRepository ivr;
	@Autowired
	CommentVoteRepository cvr;
	@Autowired
	FollowerRepository fr;
	@Autowired
	TagRepository tr;
	
	public ImageServiceImpl() {}
	
	public ImageServiceImpl(ImageRepository imageRepository) {
		this.img = imageRepository;
	}

	@Override
	public Image getImageById(Integer Id) {
		return img.read(Id);
	}
	
	@Override
	public Image getImageByAddress(String address) {
		return img.readByAddress(address);
	}

	@Override
	public List<Image> getImagesByUserId(Integer userId) {
		return img.readByUserId(userId);
	}

	@Override
	public List<Image> getAllImages(int selectionFlag, String criteria, String order, int page) {
		return img.readAll(selectionFlag, criteria, order, page);
	}
	
	public int numberOfImages(int selectionFlag, String criteria) {
		return img.numberOfImages(selectionFlag, criteria);
	}

	@Override
	public List<Image> getImagesByQuery(String query, String order, int page) {
		return img.readByQuery(query, order,  page);
	}

	@Override
	public String getRandomImage() {
		return img.randomImage();
	}

	@Override
	public List<Image> getImagesByTag(String query, String order, int page) {
		return img.readByTag(query, order, page);
	}
	
	@Override
	public List<Image> getImagesByFollowers(int userid, int page) {
		return img.readByFollowers(userid, page);
	}

	@Override
	public List<Image> getImagesByAll(String query, String order, int page) {
		
		return img.readByAll(query, order, page);
	}

	@Override
	public List<String> getNextPrevAddress(Date id, int points, String order) {
		return img.readNextPrev(id, points, order);
	}

	@Override
	public void voteImage(int iduser, int idimage, boolean voteType) {
		ivr.create(new ImageVote(null, iduser, idimage, voteType, null));
		
	}
	
	@Override
	public void voteComment(int iduser, int idcomment, boolean voteType) {
		cvr.create(new CommentVote(null, iduser, idcomment, voteType, null));
	}

	@Override
	public void saveImage(Integer idimage, int iduser, String title,
			String address, String content, int points, Date createTime, String tagsArg) {
		
		String[] tags = tagsArg.split(", ");
		
		int imageid = img.create(new Image(idimage, iduser, title, address, content, points, createTime));
		
		List<Tag> tagList = new ArrayList<Tag>();
		
		for(String tag : tags) {
			tagList.add(new Tag(null, imageid, tag));
		}
		
		tr.create(tagList);
	}

	@Override
	public void follow(int follower, int followee) {
		fr.create(new Follower(null, follower, followee));
	}
	
	public boolean isFollowing(int follower, int followee) {
		return fr.isFollowing(follower, followee);
	}

	@Override
	public boolean getToken(String uid) {
		return img.getToken(uid);
	}
	
}
