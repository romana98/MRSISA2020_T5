package com.project.tim05.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.tim05.dto.AppointmentDTO;
import com.project.tim05.dto.AppointmentRequestDTO;
import com.project.tim05.model.Appointment;
import com.project.tim05.model.AppointmentType;
import com.project.tim05.model.Clinic;
import com.project.tim05.model.Doctor;
import com.project.tim05.model.Hall;
import com.project.tim05.model.Patient;
import com.project.tim05.model.WorkCalendar;
import com.project.tim05.repository.AppointmentRespository;
import com.project.tim05.service.AppointmentService;
import com.project.tim05.service.AppointmentTypeService;
import com.project.tim05.service.ClinicAdministratorService;
import com.project.tim05.service.ClinicService;
import com.project.tim05.service.DoctorService;
import com.project.tim05.service.HallService;
import com.project.tim05.service.PatientService;
import com.project.tim05.service.WorkCalendarService;

//@CrossOrigin(origins = "https://eclinic05.herokuapp.com")

@CrossOrigin(origins = "https://localhost:4200")

@RequestMapping("/appointment")
@RestController
public class AppointmentController {

	private final AppointmentService as;
	private final DoctorService ds;
	private final HallService hs;
	private final AppointmentTypeService ats;
	private final ClinicService cs;
	private final WorkCalendarService wcs;
	private final PatientService ps;
	private final ClinicAdministratorService cas;
	private final AppointmentRespository ar;
	
	@Autowired
	public AppointmentController(AppointmentRespository ar,ClinicAdministratorService cas,PatientService ps,WorkCalendarService wcs,AppointmentService as, DoctorService ds, HallService hs, AppointmentTypeService ats,ClinicService cs) {
		super();
		this.ar = ar;
		this.as = as;
		this.ds = ds;
		this.hs = hs;
		this.ats = ats;
		this.cs = cs;
		this.wcs = wcs;
		this.ps = ps;
		this.cas = cas;
	}
	
	@PostMapping("/addAppointment")
	@PreAuthorize("hasRole('CLINIC_ADMIN') || hasRole('PATIENT')")
	public ResponseEntity<String> addAppointment(@RequestBody AppointmentDTO adto) {
		Appointment ap = new Appointment();

		
		
		Doctor dr = ds.getDoctorbyID(adto.getDoctor_id());
		Hall hall = hs.getHallbyId(adto.getHall_id());
		AppointmentType at = ats.getAppointmentTypebyId(adto.getAppointmentType_id());
		Clinic c = cs.getClinicbyId(adto.getClinic_id()) ;

		if (dr == null || hall == null || at == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

		Date date = null;
		Date wc_date = null;
		try {
			date = formatter1.parse(adto.getDate() + " " + adto.getTime());
			wc_date = formatter2.parse(adto.getDate());
		} catch (ParseException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		ap.setDateTime(date);
		ap.setDuration(adto.getDuration());
		ap.setPrice(adto.getPrice());
		ap.setRequest(adto.isRequest());
		ap.setPredefined(adto.isPredefined());
		ap.setDoctor(dr);
		ap.setAppointmentType(at);
		ap.setHall(hall);
		ap.setClinic(c);

		int flag = as.addAppointment(ap);
		//TODO u doktoru treba da se doda appointment
		

		WorkCalendar wc = new WorkCalendar();
		wc.setDate(wc_date);
		wc.setStart_time(adto.getTime());
		
		
		//racunanje minuta od pocetka dana
		String[] res = wc.getStart_time().split(":");
		int start_minutes = Integer.parseInt(res[0])*60 + Integer.parseInt(res[1]);
		//racunanje krajnjeg broja minuta od pocetka dana
		int end_minutes = start_minutes + adto.getDuration();
		//transliranje krajnjeg broja minuta nazad u oblik "hh:mm"
		//uzima se broj minuta i ostatak pri deljenju sa 60 predstavlja broj minuta koji je preko punog sata
		//a sati se dobijaju tako sto se uzme broj minuta i bez ostatka se podeli sa 60, tako dobijamo sati:minuti
		int end_minute = end_minutes%60;
		int end_hour = end_minutes/60;
		
		dr.getAppointments().add(ap);
		
		wc.setEnd_time(end_hour + ":" + end_minute);
		wc.setDoctor(dr);
		wc.setLeave(false);
		wc.setRequest(false);
		
		wcs.addCalendar(wc);
		
		for(Appointment a : dr.getAppointments()) {
			System.out.println(a.getAppointmentType().getName());
		}

		if (flag == 0)
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		else
			return ResponseEntity.status(HttpStatus.OK).body(null);

	}
	
	@PostMapping("/sendRequest")
	@PreAuthorize("hasRole('CLINIC_ADMIN') || hasRole('PATIENT')")
	public ResponseEntity<String> sendRequest(@RequestBody AppointmentRequestDTO adto){
	        
		Appointment ap = new Appointment();
		Clinic c = cs.getClinicbyId(Integer.parseInt(adto.getClinic()));
		Doctor d = ds.getDoctorbyID(Integer.parseInt(adto.getDoctor()));
		Patient p = ps.getPatientById(Integer.parseInt(adto.getPatient()));
		AppointmentType at = ats.getAppointmentTypebyId(Integer.parseInt(adto.getApp_type()));
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
		
		Date wc_date = null;
		Date date = null;
		try {
			date = formatter1.parse(adto.getDate() + " " + adto.getTime());
			wc_date = formatter2.parse(adto.getDate());
		} catch (ParseException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		
		

		ap.setDateTime(date);
		ap.setDuration(30);
		ap.setPrice(0);
		ap.setRequest(true);
		ap.setPredefined(false);
		ap.setDoctor(d);
		ap.setAppointmentType(at);
		ap.setClinic(c);
		ap.setPatient(p);
		
		WorkCalendar wc = new WorkCalendar();
		wc.setDate(wc_date);
		wc.setStart_time(adto.getTime());
		
		//racunanje minuta od pocetka dana
		String[] res = wc.getStart_time().split(":");
		int start_minutes = Integer.parseInt(res[0])*60 + Integer.parseInt(res[1]);
		//racunanje krajnjeg broja minuta od pocetka dana
		int end_minutes = start_minutes + 30;
		//transliranje krajnjeg broja minuta nazad u oblik "hh:mm"
		//uzima se broj minuta i ostatak pri deljenju sa 60 predstavlja broj minuta koji je preko punog sata
		//a sati se dobijaju tako sto se uzme broj minuta i bez ostatka se podeli sa 60, tako dobijamo sati:minuti
		int end_minute = end_minutes%60;
		int end_hour = end_minutes/60;
		
		Doctor dr = ds.getDoctorbyID(Integer.parseInt(adto.getDoctor()));
		
		Set<Appointment> a = dr.getAppointments();
		a.add(ap);
		dr.setAppointments(a);
		
		System.out.println(dr.getAppointments());
		
		String end_h = String.valueOf(end_hour);
		String end_m = String.valueOf(end_minute);
		if(end_hour < 10) {
			end_h = "0" + end_hour;
		}
		if(end_minute < 10) {
			end_m = "0" + end_minute;
		}

		wc.setEnd_time(end_h + ":" + end_m);
		wc.setDoctor(dr);
		wc.setLeave(false);
		wc.setRequest(true);
		
		wcs.addCalendar(wc);
		
		dr.getWorkCalendar().add(wc);
		
		ar.save(ap);

		int flag = as.addAppointment(ap);
		//TODO u doktoru treba da se doda appointment
		

		if (flag == 0)
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		else
			return ResponseEntity.status(HttpStatus.OK).body(null);

	}
	
	@GetMapping("/getAppointmentRequests")
	//@PreAuthorize("hasRole('CLINIC_ADMIN')")
	public ResponseEntity<Object> getAvailableHalls(@RequestParam String clinic_admin_id, String date) {
		
		ArrayList<AppointmentDTO> dtos = new ArrayList<AppointmentDTO>();
		
		int clinic_id = cas.getClinicAdmin(Integer.parseInt(clinic_admin_id)).getClinic().getId();
		
		//formiranje datuma kakav mi odgovara
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date new_date = null;
		try {
			new_date = formatter.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dtos = as.getAppointmentRequests(new_date, clinic_id);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(dtos);

	}
	
	

}
