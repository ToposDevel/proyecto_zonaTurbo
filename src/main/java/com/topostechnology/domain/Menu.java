package com.topostechnology.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "menu")
public class Menu extends BaseEntity {

	private static final long serialVersionUID = -6521750757327169499L;

	@Id
    private Long id;

    private String title;

    private String url;

    private String icon;

    @Column(name = "menu_order")
    @OrderBy
    private Integer order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_menu_id")
    private Menu parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private List<Menu> submenus;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "menu_role", joinColumns = {
            @JoinColumn(name = "menu_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

    public Menu() {
        submenus = new ArrayList<>();
    }

    public Menu(Long id, String title, String url, String icon) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.icon = icon;
        submenus = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;
        Menu menu = (Menu) o;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
