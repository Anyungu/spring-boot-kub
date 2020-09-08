package com.chef.ingredients.repos;

import com.chef.ingredients.models.Ingredients;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsRepos extends JpaRepository<Ingredients, Integer> {

}
