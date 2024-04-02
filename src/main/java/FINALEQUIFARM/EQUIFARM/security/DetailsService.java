package FINALEQUIFARM.EQUIFARM.security;

import FINALEQUIFARM.EQUIFARM.model.Employee;
import FINALEQUIFARM.EQUIFARM.model.Role;
import FINALEQUIFARM.EQUIFARM.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetailsService implements UserDetailsService {
    private EmployeeRepository employeeRepository;
    @Autowired
    public DetailsService(EmployeeRepository employeeRepository){
        this.employeeRepository= employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee user = employeeRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Username not found"));
        return new User(user.getUsername(),user.getPassword(),mapRolesToAuthorities(user.getRoles()));

    }
private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map
                ((role -> new SimpleGrantedAuthority(role.getName()))).collect(Collectors.toList());
}
}
