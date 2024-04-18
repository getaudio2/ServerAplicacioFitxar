package com.example.demo.repository;

import com.example.demo.bean.Presencia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PresenciasDAO extends JpaRepository<Presencia, Integer> {
	public List<Presencia> findByNom(String nom);
	public List<Presencia> findByFechaBetween(Date to, Date from);
	public List<Presencia> findByNomAndFechaBetween(String nom, Date to, Date from);
	//public List<Presencia> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date endDate, Date startDate);

}









