import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { SocialUserDataDto } from '../../shared/dto/socialUserDataDto.model';
import { Distrito } from '../../shared/model/distrito.model';
import { RegistroSocialRequest } from '../../shared/request/registroSocialRequest.model';
import { TipoDocumento } from '../../shared/enums/tipoDocumento.enum';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent implements OnInit {
  @Input() socialData!: SocialUserDataDto;
  @Input() tempToken!: string;
  @Input() distritos!: Distrito[];
  @Output() onComplete = new EventEmitter<RegistroSocialRequest>();
  @Output() onCancel = new EventEmitter<void>();

  socialRegisterForm!: FormGroup;
  showPassword: boolean = false;

  tiposDocumento = Object.values(TipoDocumento);

  private formBuilder = inject(FormBuilder);

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.socialRegisterForm = this.formBuilder.group({
      apePaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      apeMaterno: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      tipoDoc: [TipoDocumento.DNI, Validators.required],
      nroDoc: ['', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]],
      direccion: ['', [Validators.required, Validators.maxLength(50)]],
      idDistrito: ['', Validators.required],
      telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
      clave: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(225),
        ],
      ],
    });
  }

  onSubmit(): void {
    if (this.socialRegisterForm.invalid) {
      this.markFormGroupTouched(this.socialRegisterForm);
      return;
    }

    const formValue = this.socialRegisterForm.value;

    const request: RegistroSocialRequest = {
      tempToken: this.tempToken,
      nombres: this.socialData.nombre,
      correo: this.socialData.email,
      imagen: this.socialData.avatar,
      provider: this.socialData.provider,
      apePaterno: formValue.apePaterno,
      apeMaterno: formValue.apeMaterno,
      tipoDocumento: formValue.tipoDoc,
      nroDoc: formValue.nroDoc,
      telefono: formValue.telefono,
      direccion: formValue.direccion,
      idDistrito: parseInt(formValue.idDistrito),
      clave: formValue.clave,
    };

    this.onComplete.emit(request);
  }

  cancelar(): void {
    this.onCancel.emit();
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((key) => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.socialRegisterForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.socialRegisterForm.get(fieldName);

    if (field?.hasError('required')) {
      return 'Este campo es requerido';
    }
    if (field?.hasError('minlength')) {
      return `Mínimo ${field.errors?.['minlength'].requiredLength} caracteres`;
    }
    if (field?.hasError('maxlength')) {
      return `Máximo ${field.errors?.['maxlength'].requiredLength} caracteres`;
    }
    if (field?.hasError('pattern')) {
      if (fieldName === 'nroDoc') {
        return 'Debe tener 8 dígitos';
      }
      if (fieldName === 'telefono') {
        return 'Debe tener 9 dígitos';
      }
    }
    return '';
  }
}
