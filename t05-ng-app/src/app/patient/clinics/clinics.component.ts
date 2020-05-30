import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-clinics',
  templateUrl: './clinics.component.html',
  styleUrls: ['./clinics.component.css']
})
export class ClinicsComponent implements OnInit {

  displayedColumns: string[] = ['name', 'address', 'avg_rating', 'price'];

  dataSource = new MatTableDataSource();

  selectedRowIndex: number = 0;

  appointmentType_id : number = 0;

  appointmentTypes : any=[];

  model : ClinicModel = {
    id : 0,
    name : '',
    address : '',
    avg_rating : 0,
    price : 0
  }

  searchModel : SearchClinicModel = {
    date_field : null,
    date : '',
    appointmentType_id : 0,
    address : '',
    avg_rate_lowest : 0,
    avg_rate_highest : 0
  }

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

  constructor(private _snackBar: MatSnackBar, private http: HttpClient, private router: Router) { }

  ngOnInit(): void {
    /*let params = new HttpParams();
    params = params.append('date', "0")
    params = params.append('appointmentType_id', this.searchModel.appointmentType_id.toString());
    params = params.append('address', "0");
    params = params.append('avg_rate_lowest', "0");
    params = params.append('avg_rate_highest', "0");

    this.http.get("http://localhost:8081/patients/getClinics",{params:params})
    .subscribe((res) => {
      // @ts-ignore
      this.dataSource.data = res;

    });*/


    this.dataSource.paginator = this.paginator;
    this.selectedRowIndex  = 1;

    this.http.get("http://localhost:8081/appointmentType/getAllApointmentTypes").subscribe(
      (res) =>{
        this.appointmentTypes = res;
      }
    );

  }

  search(): void{
    this.searchModel.date = this.searchModel.date_field.getDate() + "/" + (this.searchModel.date_field.getMonth()+1) + "/" + this.searchModel.date_field.getFullYear();
    let params = new HttpParams();    
    params = params.append('date', this.searchModel.date.toString())
    params = params.append('appointmentType_id', this.searchModel.appointmentType_id.toString());
    this.http.get("http://localhost:8081/patients/getClinics",{params:params})
    .subscribe((res) => {
      // @ts-ignore
      this.dataSource.data = res;

    });
  }

  findDoctors(row){
    this.router.navigate(['/patient/doctors'],{queryParams : {'clinic_id' : row.id,
                                                              'appointment_type_id': this.searchModel.appointmentType_id,
                                                              'date' : this.searchModel.date.toString()}});
  }

}

export interface ClinicModel{
   id : number,
   name: String,
   address : String,
   avg_rating : number,
   price : number
}

export interface SearchClinicModel{
  date_field : Date,
  date : String,
  appointmentType_id : number,
  address : String,
  avg_rate_lowest : number,
  avg_rate_highest : number
}
