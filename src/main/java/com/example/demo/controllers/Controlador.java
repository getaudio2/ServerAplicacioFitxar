package com.example.demo.controllers;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.bean.Presencia;
import com.example.demo.bean.Usuario;
import com.example.demo.repository.PresenciasDAO;

@Controller  //Lo convertimos en un servlet atiende peticiones http
@RequestMapping("")   //localhost:8080 
public class Controlador {

	@Autowired
	PresenciasDAO presenciasDAO; 

	@GetMapping("/") //Da salida al formulario de login
	public String iniciar(){
		return "login";
	}
	@PostMapping("/")
	public String login(Usuario usuario, Model model) {
		//if (bd.compruebaUsuario(usuario.getNombre(),usuario.getPassword())) {
		if (usuario.getNom().equals("admin") && usuario.getPassword().equals("admin")) {
			List<Presencia> presencias = presenciasDAO.findAll(); 
			model.addAttribute("presencias",presencias);
			model.addAttribute("presencia",new Presencia());
			return "consulta";
		}else {
			return "login";
		}
	}
	@GetMapping(value="listar")//android web
	public ResponseEntity<List<Presencia>> getPresencias(){
		List<Presencia> presencias = presenciasDAO.findAll();
		return ResponseEntity.ok(presencias);
	}
	@GetMapping(value="listar/{name}")//android web
	public ResponseEntity<List<Presencia>> getPresenciasBy(@PathVariable("name") String name){
		List<Presencia> presencias = presenciasDAO.findByNom(name);
		return ResponseEntity.ok(presencias);
	}
	@PostMapping("insertar") //
	public ResponseEntity<Presencia> crearPresencia(String nom, double latitud, double longitud){
		//double latAustria=41.41694549369084;
		//double longAustria=2.1989492153444297;
		double latAustria = 41.4161732;
		double longAustria = 2.1991057;
		String comentario="";
		int distancia= (int)distancia2(latAustria, longAustria, latitud, longitud);
		boolean esta_dentro=distancia<25; 
		if (esta_dentro) comentario="Ha fichado correctamente";
		else comentario="No ha fichado correctamente";
		Presencia pre=new Presencia(nom, latitud, longitud, distancia,comentario);
		Presencia prenew= presenciasDAO.save(pre);
		return ResponseEntity.ok(prenew);
	}

	@PostMapping("/filtrar")//Web
	public String filtrar(String nom, String fecha, Model model) throws ParseException {	
		List<Presencia> presencias;
		if (nom.equals("") && fecha.equals(""))
			presencias = presenciasDAO.findAll();
		else if (!nom.equals("") && fecha.equals(""))
			presencias = presenciasDAO.findByNom(nom);
		else if (nom.equals("") && !fecha.equals("")) {
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date1 = formatter1.parse(fecha+" 00:00:00");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date2 = formatter2.parse(fecha+" 23:59:59");
			presencias = presenciasDAO.findByFechaBetween(date1,date2);
		}
		else {
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date1 = formatter1.parse(fecha+" 00:00:00");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date2 = formatter2.parse(fecha+" 23:59:59");
			presencias = presenciasDAO.findByNomAndFechaBetween(nom,date1, date2);
		}
		model.addAttribute("presencias",presencias);
		return "consulta";		
	}
	
	@GetMapping("/borrado/{id}") 
	public String borrar(@PathVariable int id, Model model) {	
		presenciasDAO.deleteById(id);	
		List<Presencia> presencias = presenciasDAO.findAll(); 
		model.addAttribute("presencias",presencias);
		return "consulta";		
	}
	
    public static double distancia1(double lat1, double long1, double lat2, double long2) {
        return 6371*Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - (long1)));

    }
    public static double distancia2(double lat1, double lng1, double lat2, double lng2) {
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia*1000;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double distancia3(double lat1,double lon1,double lat2, double lon2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * 6378137;
        //s = Math.round(s * 10000) / 10000;
        return s;
    }
}