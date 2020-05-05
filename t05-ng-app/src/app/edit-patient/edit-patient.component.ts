import {Component, Directive, Input, OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {MatSnackBar} from "@angular/material/snack-bar";
import {AbstractControl, FormControl, NG_VALIDATORS, Validator, ValidatorFn, Validators} from "@angular/forms";


@Component({
    selector: 'app-edit-patient',
    templateUrl: './edit-patient.component.html',
    styleUrls: ['./edit-patient.component.css']
})
export class EditPatientProfile implements OnInit{

    model: patientModel = {
        email : '',
        password : '',
        name : '',
        surname : '',
        address : '',
        city : '',
        country : '',
        phone_number : '',
        insurance_number : ''
    }
  hide: boolean;

    constructor(private _snackBar: MatSnackBar, private http: HttpClient){

    }

    ngOnInit(): void{
        this.hide = true;
        let url = "http://localhost:8081/patients/getPatient";
        this.http.get(url).subscribe(
          res => {
            this.model = <patientModel>res;
            this.model.password = '';
          }
        )
    }

    editPatient(): void{
        let url = "http://localhost:8081/patients/editPatient"
        this.http.post(url,this.model).subscribe(
            res => {
              this._snackBar.open("Your profile has been updated successfully!", "Close", {
                duration: 2000,
              });

            },
            err => {
              this._snackBar.open("Error has occurred while updating your profile!", "Close", {
                duration: 2000,
              });
              console.log(err)
            }
        );
    }
  checkPassword() {

    return this.model.password.length == 0 || this.model.password.length >= 8;
  }
}
    export interface patientModel{
        email : string |RegExp;
        password : string;
        name : string |RegExp;
        surname : string |RegExp;
        address : string |RegExp;
        city : string |RegExp;
        country : string |RegExp;
        phone_number : string |RegExp;
        insurance_number : string |RegExp;
    }

@Directive({
  selector: '[requiredLen]',
  providers: [
    {provide: NG_VALIDATORS,useExisting:RequiredPassDirective, multi: true}
  ]
})
export class RequiredPassDirective implements Validator {
  @Input("requiredLen")
  requiredLen: boolean;

  validate(c:AbstractControl) {

    let value = c.value;
    if (value == null) value = '';
    if ((value.length > 0 && value.length < 8)) {
      return {
        requiredLen: {condition:false}
      };
    }
    return null;
  }

}

