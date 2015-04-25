package wto.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import wto.model.Comment;
import wto.model.CustomUserDetails;
import wto.model.Image;
import wto.model.User;
import wto.service.CommentService;
import wto.service.ImageService;
import wto.service.UserService;

@Controller
public class HelloController{
	@Autowired
    ImageService imageService;
	@Autowired
	UserService userService;
	@Autowired
	CommentService commentService;
	
	public String imagePage(Model model, Image theImage, List<String> nextprev, String order) {
		int mostPoints = 0;
        
        if(theImage.getComments().size() > 0)
        	 mostPoints = theImage.getComments().get(0).getPoints();
        
        model.addAttribute("ImageId", theImage.getIdimage());
        model.addAttribute("ImageLocation", theImage.getContent());
        model.addAttribute("ImageCreateTime", theImage.getCreateTimeAsString());
        model.addAttribute("ImagePoints", theImage.getPoints());
        model.addAttribute("ImageTitle", theImage.getTitle());
        model.addAttribute("ImageUser", theImage.getUser().getUsername());
        
        model.addAttribute("NextImage", nextprev.get(0));
        model.addAttribute("PrevImage", nextprev.get(1));
        
        model.addAttribute("Order", order);
        
        model.addAttribute("Comments", theImage.getComments());
        model.addAttribute("Tags", theImage.getTags());
        model.addAttribute("MostPoints", mostPoints);
		return "image";
	}
	
	public String userPage(Model model, String userName) {        
        User theUser = userService.getUserByNameAndFetch(userName);
        
        List<Comment> comments = theUser.getComments();
        List<Image> images = theUser.getImages();
        
        model.addAttribute("UserName",theUser.getUsername());
		model.addAttribute("UserPoints", theUser.getPoints());
		model.addAttribute("ImageNumber", images.size());
		model.addAttribute("CommentNumber", comments.size());
		
		model.addAttribute("Images", images);
		model.addAttribute("Comments", comments);
		return "user";
			
	}
	
	public String indexPage(Model model, String order) {
        
        List<Image> images = imageService.getAllImages(order);
        
        model.addAttribute("Images", images);
        
        return "index";
	}
	
	public String searchPageWithTitle(Model model, String query, String order) {
		List<Image> images = imageService.getImagesByQuery(query, order);
		model.addAttribute("Images", images);
		model.addAttribute("Query", "title:" + query);
		return "search";
	}
	
	public String searchPageWithTag(Model model, String query, String order) {
		List<Image> images = imageService.getImagesByTag(query, order);
		model.addAttribute("Images", images);
		model.addAttribute("Query", "tag:" + query);
		model.addAttribute("Order", order);
		return "search";
	}
	
	public String searchPageWithAll(Model model, String query, String order) {
		Set<Image> images = imageService.getImagesByAll(query, order);
		model.addAttribute("Images", images);
		model.addAttribute("Query", query);
		model.addAttribute("Order", order);
		return "search";
	}
	
	public String searchPageFlagHandler(Model model, String query, String order) {
		String orderStatement = "ORDER BY i.points DESC";
		
		if(query.startsWith("user:")) {
			query = query.replace("user:", "");
			return "redirect:/user/" + query;
		}
		else if(query.startsWith("title:")) {
			if(order.equals("bypoints"))
				orderStatement = "ORDER BY i.points DESC";
			else if(order.equals("byrandom"))
				orderStatement = "ORDER BY rand()";
			else if(order.equals("bynewest"))
				orderStatement = "ORDER BY i.createTime DESC";
			else
				orderStatement = "";
			query = query.replace("title:", "");
			return searchPageWithTitle(model, query, orderStatement);
		}
		else if(query.startsWith("tag:")) {
			if(order.equals("bypoints"))
				orderStatement = "ORDER BY t.image.points DESC";
			else if(order.equals("byrandom"))
				orderStatement = "ORDER BY rand()";
			else if(order.equals("bynewest"))
				orderStatement = "ORDER BY t.image.createTime DESC";
			else
				orderStatement = "";
			query = query.replace("tag:", "");
			return searchPageWithTag(model, query, orderStatement);
		}
		else {
			return searchPageWithAll(model, query, order);
		}
		
	}
	
	
	
	// Mapper functions
	
	@RequestMapping(value = "/user/{userName}", method = RequestMethod.GET)
    public String userImagesPageMapper(@PathVariable("userName") String userName, Model model) {
        return userPage(model, userName);
    }
	
	@RequestMapping(value = "/image/{address}", method = RequestMethod.GET)
    public String imagePageMapper(@PathVariable("address") String address, Model model) {
        
        Image theImage = imageService.getImageByAddress(address);
        
        List<String> nextprev = imageService.getNextPrevAddress(theImage.getCreateTime(), theImage.getPoints(), "ORDER BY i.createTime DESC");
        
        return imagePage(model, theImage, nextprev, "");
    }
	
	@RequestMapping(value = "/image/{address}/{order}", method = RequestMethod.GET)
    public String imageOrderedPageMapper(@PathVariable("address") String address, @PathVariable("order") String order, Model model) {
        
        Image theImage = imageService.getImageByAddress(address);
        
        List<String> nextprev = imageService.getNextPrevAddress(theImage.getCreateTime(), theImage.getPoints(), order);
        
        return imagePage(model, theImage, nextprev, "/" + order);
    }
	
	@RequestMapping(value = "/image/random", method = RequestMethod.GET)
    public String imageRandomPageMapper(Model model) {
        
        String theImage = imageService.getRandomImage();

        return "redirect:/image/" + theImage;
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
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView searchMapper(@RequestParam("query") String query) {
		return new ModelAndView(new RedirectView("search/" + query));
	}
	
	@RequestMapping(value = "/search/{query}/{order}", method = RequestMethod.GET)
	public String searchPageOrderedMapper(@PathVariable("query") String query, @PathVariable("order") String order, Model model) {
		return searchPageFlagHandler(model, query, order);
	}
	
	@RequestMapping(value = "/search/{query}", method = RequestMethod.GET)
	public String searchPageMapper(@PathVariable("query") String query, Model model) {
		return searchPageFlagHandler(model, query, "");
	}
	
	@PreAuthorize("hasRole('ROLE_ANONYMOUS')")
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPageMapper() {
		return "login";
	}
	
	@PreAuthorize("hasRole('ROLE_ANONYMOUS')")
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String registerPageMapper(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "register";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerPageProcess(@RequestParam("username") String username, 
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("password2") String password2) {
		
		PasswordEncoder pe = new BCryptPasswordEncoder();
		password = pe.encode(password);
		userService.saveUser(new User(null,username,email,password,0,null));
		return "register";
			
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/comment", method = RequestMethod.POST)
	public String commentProcess(@RequestParam("content") String content, @RequestParam("imageId") int idimage) {
	    CustomUserDetails user =  (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
	    
		commentService.saveComment(new Comment(null, user.getUser().getIduser(), idimage, content, 0, null));
		return "redirect:/";
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value="/upload", method = RequestMethod.GET)
	public String uploadPageMapping(Model model) {
		return "upload";
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(@RequestParam("title") String title,
			@RequestParam("tags") String tags,
            @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(title)));
                stream.write(bytes);
                stream.close();
                return "index";
            } catch (Exception e) {
                return "user";
            }
        } else {
            return "user";
        }
    }
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value="/image_vote", method = RequestMethod.POST)
	public String imageVoteProcess(@RequestParam boolean voteType, @RequestParam int imageId) {
	    CustomUserDetails user =  (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  

	    imageService.voteImage(user.getUser().getIduser(), imageId, voteType);
	    
	    if(voteType)
	    	return "user";
	    else
	    	return "index";
	}

}