package wto.controller;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import wto.model.Comment;
import wto.model.Image;
import wto.model.User;
import wto.repository.UserRepositoryImpl;
import wto.service.ImageServiceImpl;
import wto.service.UserServiceImpl;

@Controller
public class HelloController{
	
	public String imagePage(Model model, Image theImage) {
		int mostPoints = 0;
        
        if(theImage.getComments().size() > 0)
        	 mostPoints = theImage.getComments().get(0).getPoints();
        
        model.addAttribute("ImageId", theImage.getIdimage());
        model.addAttribute("ImageLocation", theImage.getContent());
        model.addAttribute("ImageCreateTime", theImage.getCreateTimeAsString());
        model.addAttribute("ImagePoints", theImage.getPoints());
        model.addAttribute("ImageTitle", theImage.getTitle());
        
        model.addAttribute("Comments", theImage.getComments());
        model.addAttribute("MostPoints", mostPoints);
		return "image";
	}
	
	public String userPage(Model model, String userName, boolean isImages) {
		UserServiceImpl userService = new UserServiceImpl();
        
        User theUser = userService.getUserByName(userName);
        
        List<Comment> comments = theUser.getComments();
        List<Image> images = theUser.getImages();
        
        model.addAttribute("UserName",theUser.getUsername());
		model.addAttribute("UserPoints", theUser.getPoints());
		model.addAttribute("ImageNumber", images.size());
		model.addAttribute("CommentNumber", comments.size());
		
		if(isImages) {
			model.addAttribute("Images", images);
			return "userImages";
		}
		else {
			model.addAttribute("Comments", comments);
			return "userComments";
		}		
	}
	
	public String indexPage(Model model, String order) {
		ImageServiceImpl imageService = new ImageServiceImpl();
        
        List<Image> images = imageService.getAllImages(order);
        
        model.addAttribute("Images", images);
        
        return "index";
	}
	
	public String searchPageWithTitle(Model model, String query, String order) {
		ImageServiceImpl imageService = new ImageServiceImpl();
		List<Image> images = imageService.getImagesByQuery(query, order);
		model.addAttribute("Images", images);
		model.addAttribute("Query", "title:" + query);
		return "search";
	}
	
	public String searchPageWithTag(Model model, String query, String order) {
		ImageServiceImpl imageService = new ImageServiceImpl();
		List<Image> images = imageService.getImagesByTag(query, order);
		model.addAttribute("Images", images);
		model.addAttribute("Query", "tag:" + query);
		return "search";
	}
	
	public String searchPageWithAll(Model model, String query, String order) {
		ImageServiceImpl imageService = new ImageServiceImpl();
		Set<Image> images = imageService.getImagesByAll(query, order);
		model.addAttribute("Images", images);
		model.addAttribute("Query", query);
		return "search";
	}
	
	public String searchPageFlagHandler(Model model, String query, String order) {
		String orderStatement;
		if(order == "bypoints")
			orderStatement = "ORDER BY i.points DESC";
		else if(order == "byrandom")
			orderStatement = "ORDER BY rand()";
		else if(order == "bynewest")
			orderStatement = "ORDER BY i.createTime DESC";
		else
			orderStatement = "";
		
		if(query.startsWith("user:")) {
			query = query.replace("user:", "");
			return "redirect:/user/" + query + "/images";
		}
		else if(query.startsWith("title:")) {
			query = query.replace("title:", "");
			return searchPageWithTitle(model, query, orderStatement);
		}
		else if(query.startsWith("tag:")) {
			query = query.replace("tag:", "");
			return searchPageWithTag(model, query, orderStatement);
		}
		else {
			return searchPageWithAll(model, query, order);
		}
	}
	
	
	
	// Mapper functions
	
	@RequestMapping(value = "/user/{userName}/images", method = RequestMethod.GET)
    public String userImagesPageMapper(@PathVariable("userName") String userName, Model model) {
        return userPage(model, userName, true);
    }
	
	@RequestMapping(value = "/user/{userName}/comments", method = RequestMethod.GET)
    public String userCommentsPageMapper(@PathVariable("userName") String userName, Model model) {
        return userPage(model, userName, false);
    }
	
	@RequestMapping(value = "/image/{imageId}", method = RequestMethod.GET)
    public String imagePageMapper(@PathVariable("imageId") Integer imageId, Model model) {
        ImageServiceImpl imageService = new ImageServiceImpl();
        
        Image theImage = imageService.getImageById(imageId);
        
        return imagePage(model, theImage);
    }
	
	@RequestMapping(value = "/image/random", method = RequestMethod.GET)
    public String imageRandomPageMapper(Model model) {
        ImageServiceImpl imageService = new ImageServiceImpl();
        
        Image theImage = imageService.getRandomImage();

        return imagePage(model, theImage);
    }
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	 public String indexPageMapper(Model model) {
        return indexPage(model, "ORDER BY i.createTime DESC");
    }
	
	@RequestMapping(value = "/bypoints", method = RequestMethod.GET)
	 public String indexPageByPointsMapper(Model model) {
		return indexPage(model, "ORDER BY i.points DESC");
	}
	
	@RequestMapping(value = "/byrandom", method = RequestMethod.GET)
	 public String indexPageByRandom(Model model) {
		return indexPage(model, "ORDER BY rand()");
	}
	
	@RequestMapping(value = "/search/{query}/{order}", method = RequestMethod.GET)
	 public String searchPage(@PathVariable("query") String query, @PathVariable("order") String order, Model model) {
		return searchPageFlagHandler(model, query, order);
	}
	
	@RequestMapping(value = "/search/{query}", method = RequestMethod.GET)
	 public String searchPage(@PathVariable("query") String query, Model model) {
		return searchPageFlagHandler(model, query, "");
	}
	
	@RequestMapping("/test")
	public String printTest(ModelMap model) {
		UserRepositoryImpl userRepository = new UserRepositoryImpl();
		model.addAttribute("message", "Username: " + userRepository.read(4).getUsername() + "<br>Pass: " + userRepository.read(4).getPassword() + "<br>Email: " + userRepository.read(4).getEmail() + "<br>Points: " + userRepository.read(4).getPoints() + "<br>Create Time: " + userRepository.read(4).getCreateTime());
	
		return "test";
	}

}