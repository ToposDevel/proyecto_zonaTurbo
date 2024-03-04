package com.topostechnology.security;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topostechnology.domain.Menu;
import com.topostechnology.mapper.UserMapper;
import com.topostechnology.model.UserModel;
import com.topostechnology.service.MenuService;
import com.topostechnology.service.UserService;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private MenuService menuService;

    @Resource
    private HttpSession session;
    
    @Resource
    private UserService userService;
    
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		com.topostechnology.domain.User user = userService.findByUserName(userName);
		if (user == null)
			throw new UsernameNotFoundException("Usuario no encontrado");
		UserModel userModel = UserMapper.convertUserToUserModel(user);
		userModel.setPassword(user.getPassword());
		session.setAttribute("user", userModel);
		if (user.getRole() != null) {
			session.setAttribute("menus", buildMenuList(menuService.getMenusByRole(user.getRole()))); // TODO 
		} else {
			session.setAttribute("menus", new ArrayList<>());
		}
		return this.buildUserForAuthentication(userModel);
	}

    private List<Menu> buildMenuList(List<Menu> menusByRole) {
        List<Menu> menus = new ArrayList<>();
        for (Menu menu : menusByRole) {
            if (menu.getParent() == null) {
                menus.add(new Menu(menu.getId(), menu.getTitle(), menu.getUrl(), menu.getIcon()));
            } else {
                Menu parent = getParentMenu(menus, menu.getParent());
                if (parent == null) {
                    menus.add(new Menu(menu.getId(), menu.getTitle(), menu.getUrl(), menu.getIcon()));
                } else {
                    parent.getSubmenus().add(new Menu(menu.getId(), menu.getTitle(), menu.getUrl(), menu.getIcon()));
                }
            }
        }
        return menus;
    }

    private Menu getParentMenu(List<Menu> menus, Menu example) {
        for (Menu menu : menus) {
            if (menu.equals(example)) {
                return menu;
            }
        }
        return null;
    }

    private User buildUserForAuthentication(UserModel user) {
    	String username = user.getUserName();
        String password = user.getPassword();
        
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRoleName()));
        boolean status = user.isActive();
        
        return new User(username, password, status, true, !user.isResetPassword(),
                true, roles);
    }

}
