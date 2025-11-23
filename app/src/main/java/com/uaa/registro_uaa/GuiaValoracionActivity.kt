
package com.uaa.registro_uaa

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Element
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.Phrase
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.PdfWriter


class GuiaValoracionActivity : AppCompatActivity() {

    private lateinit var etIdEstudiante: EditText
    private lateinit var etNombre: EditText
    private lateinit var etEdad: EditText
    private lateinit var rgSexo: RadioGroup
    private lateinit var religion: EditText
    private lateinit var escolaridad: EditText
    private lateinit var ocupacion: EditText
    private lateinit var estadoCivil: EditText
    private lateinit var tvFechaIngreso: TextView
    private lateinit var rgPiso: RadioGroup
    private lateinit var noVentanas: RadioGroup

    //Piso
    private lateinit var cbBloque: CheckBox
    private lateinit var cbLadrillo: CheckBox
    private lateinit var cbAdobe: CheckBox
    private lateinit var cbLamina: CheckBox
    private lateinit var cbMadera: CheckBox
    private lateinit var cbParedVarias: CheckBox

    //techo
    private lateinit var tcConcreto: CheckBox
    private lateinit var tcLamina: CheckBox
    private lateinit var tcTeja: CheckBox
    private lateinit var tcCarton: CheckBox
    private lateinit var luz: RadioGroup

    //abastecimiento de agua
    private lateinit var cbLlave: CheckBox
    private lateinit var cbPozo: CheckBox
    private lateinit var cbPipa: CheckBox
    private lateinit var cbOtroAgua: CheckBox
    private lateinit var etAguaOtro: EditText

    //purificacionAgua
    private lateinit var meGarrafon: CheckBox
    private lateinit var meHervida: CheckBox
    private lateinit var meClorada: CheckBox
    private lateinit var meNinguno: CheckBox
    private lateinit var meMasDeUno: CheckBox

    //Drenaje y Alcantarillado
    private lateinit var dcDrenaje: CheckBox
    private lateinit var dcLetrina: CheckBox
    private lateinit var dcFosa: CheckBox
    private lateinit var dcSuelo: CheckBox

    //basura
    private lateinit var trCamion: CheckBox
    private lateinit var trQuema: CheckBox
    private lateinit var trEnterrar: CheckBox
    private lateinit var trMasDeUno: CheckBox
    private lateinit var trNinguno: CheckBox

    //fauna nosiva
    private lateinit var faRatones: CheckBox
    private lateinit var faMoscas: CheckBox
    private lateinit var faMosquitos: CheckBox
    private lateinit var faArañas: CheckBox
    private lateinit var faCucarachas: CheckBox
    private lateinit var faPulgas: CheckBox
    private lateinit var faMasDeUno: CheckBox
    private lateinit var faNinguno: CheckBox


    //animales domesticos
    private lateinit var anPerros: CheckBox
    private lateinit var anGatos: CheckBox
    private lateinit var anAves: CheckBox
    private lateinit var anCerdos: CheckBox
    private lateinit var anMasDeUno: CheckBox
    private lateinit var anOtro: CheckBox
    private lateinit var anNinguno: CheckBox

    //numeros de animales domesticos RADIOBUTH
    private lateinit var noAnimalesDomesticos: RadioGroup

    //animales domesticos vacunados
    private lateinit var animalesVacunados: RadioGroup

    ///////////////////////////////SECCION 2

    //relacion familiar
    private lateinit var relacionFamiliar: RadioGroup

    //ingreso economico familiar
    private lateinit var ie1sm: CheckBox
    private lateinit var ie12sm: CheckBox
    private lateinit var ie3sm: CheckBox
    private lateinit var ieninguno: CheckBox

    //dependencia economica
    private lateinit var dependenciaEconomica: RadioGroup

    //estado nutricional
    private lateinit var nuDesnutricion: CheckBox
    private lateinit var nuNormal: CheckBox
    private lateinit var nuSobrepeso: CheckBox
    private lateinit var nuObesidad: CheckBox

    //cabello
    private lateinit var caquebradizo: CheckBox
    private lateinit var caopaco: CheckBox
    private lateinit var caescaso: CheckBox
    private lateinit var camasdeuno: CheckBox
    private lateinit var casaludable: CheckBox

    //mucosa
    private lateinit var muSangrado: CheckBox
    private lateinit var muInfeccion: CheckBox
    private lateinit var muPalidez: CheckBox
    private lateinit var muSaludable: CheckBox

    //piel
    private lateinit var piPielGallina: CheckBox
    private lateinit var piSecaQuebrada: CheckBox
    private lateinit var piPuntosNegros: CheckBox
    private lateinit var piSecaMaltratada: CheckBox
    private lateinit var piEscamosa: CheckBox
    private lateinit var piPalidez: CheckBox
    private lateinit var pimasdeuno: CheckBox
    private lateinit var piSaludable: CheckBox

    //labios
    private lateinit var laLesiones: CheckBox
    private lateinit var laSecos: CheckBox
    private lateinit var laAgrietados: CheckBox
    private lateinit var laMasdeuno: CheckBox
    private lateinit var laSaludables: CheckBox

    //encias
    private lateinit var enHinchadas: CheckBox
    private lateinit var enSangran: CheckBox
    private lateinit var enMasdeUno: CheckBox
    private lateinit var enSaludable: CheckBox

    //nariz y orejas
    private lateinit var orLesiones: CheckBox
    private lateinit var orHumedas: CheckBox
    private lateinit var orEnrojecidas: CheckBox
    private lateinit var orMasdeuno: CheckBox
    private lateinit var orSaludables: CheckBox

    //uñas
    private lateinit var asPalidas: CheckBox
    private lateinit var asQuebradas: CheckBox
    private lateinit var asEstriadas: CheckBox
    private lateinit var asMasdeuno: CheckBox
    private lateinit var asSaludables: CheckBox

    //sistema oseo
    private lateinit var soEncorvado: CheckBox
    private lateinit var soReduccion: CheckBox
    private lateinit var soDecalcificacion: CheckBox
    private lateinit var soMasdeuno: CheckBox
    private lateinit var soSaludable: CheckBox

    // en general
    private lateinit var geDebilidad: CheckBox
    private lateinit var geFatiga: CheckBox
    private lateinit var geCansancio: CheckBox
    private lateinit var geCianosis: CheckBox
    private lateinit var geMasdeuno: CheckBox
    private lateinit var geSaludable: CheckBox

    //variacion ultimos 6 meses
    private lateinit var vpPeso: RadioGroup

    //kilos ha subido
    private lateinit var kisubido: RadioGroup

    //kilos perdidos
    private lateinit var kpPerdido: RadioGroup

    //dentadura
    private lateinit var cdCompleta: CheckBox
    private lateinit var cdFrenos: CheckBox
    private lateinit var cdProtesis: CheckBox
    private lateinit var cdMaloclusion: CheckBox
    private lateinit var cdMasdeuno: CheckBox

    //problema de cavidad oral
    private lateinit var coSi: CheckBox
    private lateinit var coNo: CheckBox
    private lateinit var etCavidadDetalle: EditText

    //problemas dentales
    private lateinit var deSi: CheckBox
    private lateinit var deNo: CheckBox
    private lateinit var etProblemasDentales: EditText

    //problemasParaDigerirAlimentos
    private lateinit var daSi: CheckBox
    private lateinit var daNo: CheckBox
    private lateinit var et_dijerirAlimentos: EditText

    //alimenos que no puede comer
    private lateinit var alderivadosLeche: CheckBox
    private lateinit var alcarnes: CheckBox
    private lateinit var alazucares: CheckBox
    private lateinit var algrasas: CheckBox
    private lateinit var alsales: CheckBox
    private lateinit var alcolesterol: CheckBox
    private lateinit var almasdeuno: CheckBox
    private lateinit var alotros: CheckBox
    private lateinit var alcometodo: CheckBox

    //con que guiza sus alimentos
    private lateinit var agaceiteVegetal: CheckBox
    private lateinit var agaceiteAnimal: CheckBox
    private lateinit var agmanteca: CheckBox

    //come en desayuno
    private lateinit var cmlechecafe: CheckBox
    private lateinit var cmpancereal: CheckBox
    private lateinit var cmfrutatamales: CheckBox
    private lateinit var cmhuevoquesadillas: CheckBox
    private lateinit var cmrefrescojugo: CheckBox

    //come comida
    private lateinit var ccrefrecoagua: CheckBox
    private lateinit var ccverdurastacos: CheckBox
    private lateinit var ccsopaarroz: CheckBox
    private lateinit var cccarnesoya: CheckBox
    private lateinit var ccfrijolesgorditas: CheckBox

    //que come en la cena
    private lateinit var qclechecafe: CheckBox
    private lateinit var qctacosfruta: CheckBox
    private lateinit var qcyogurthtacos: CheckBox

    //cepillado de dientes
    private lateinit var cddiario: CheckBox
    private lateinit var cddosdias: CheckBox
    private lateinit var cdtresdias: CheckBox
    private lateinit var cdcuatrodias: CheckBox
    private lateinit var cdesporadicamente: CheckBox
    private lateinit var cdnunca: CheckBox

    //se baña
    private lateinit var sbdiario: CheckBox
    private lateinit var sbdosdias: CheckBox
    private lateinit var sbtresdias: CheckBox

    //cambio de ropa
    private lateinit var cpdiario: CheckBox
    private lateinit var cpdosdias: CheckBox
    private lateinit var cptresdias: CheckBox

    //lavado de manos
    private lateinit var lmantescomer: CheckBox
    private lateinit var lmdespuesbano: CheckBox
    private lateinit var lmantespreparar: CheckBox
    private lateinit var lmmasdeunavez: CheckBox
    private lateinit var lmaveces: CheckBox

    ///////////////////////SECCION 3

    // servicios de salud
    private lateinit var seimss: CheckBox
    private lateinit var seissste: CheckBox
    private lateinit var seisea: CheckBox
    private lateinit var separticular: CheckBox
    private lateinit var seseguropopular: CheckBox
    private lateinit var seotro: CheckBox
    private lateinit var etServicioSocial: EditText

    //cuando acude al servicio medico
    private lateinit var acServicioMedico: RadioGroup

    //capaz de tomar desiciones respecto a su tratamiento
    private lateinit var drSi: CheckBox
    private lateinit var drNo: CheckBox
    private lateinit var etDecisiones: EditText

    //presencia de alguna enfermedad
    private lateinit var peSi: CheckBox
    private lateinit var peNo: CheckBox
    private lateinit var etPresenciaEnfermedad: EditText

    //tiene tratamiento
    private lateinit var trSi: CheckBox
    private lateinit var trNo: CheckBox
    private lateinit var et_tratamiento: EditText

    //lleva a cabo su tratamiento
    private lateinit var ctSi: CheckBox
    private lateinit var ctNo: CheckBox

    ////////////////   PARTE II MANTENIMIENTO DE UN APORTE DE AIRE SUFICIENTE

    //frecuencias respiratorias
    private lateinit var frrespiratorias: EditText
    private lateinit var frcianosis: CheckBox
    private lateinit var etfrecuenciasRespiratorias: EditText



    //cianosis
    private lateinit var ciaSi: CheckBox
    private lateinit var ciaNo: CheckBox
    private lateinit var ciaEspecifique: EditText

    //llenado capilar
    private lateinit var lcSi: CheckBox
    private lateinit var lcNo: CheckBox

    //fuma
    private lateinit var fuSi: CheckBox
    private lateinit var fuNo: CheckBox

    //no. cigarrillos al dia
    private lateinit var csCigarrillos: EditText


    //convive con fumadores
    private lateinit var cnSi: CheckBox
    private lateinit var cnNo : CheckBox


    //tos frecuente
    private lateinit var tfSi: CheckBox
    private lateinit var tfNo: CheckBox

    //ruidos respiratorios anormales
    private lateinit var raSi: CheckBox
    private lateinit var raNo: CheckBox

    //Factores desencadenantes de alteraciones respiratorias
    private lateinit var etFactoresAlteracion: EditText

    //oxigenoterapia
    private lateinit var oxpuntasnasales: CheckBox
    private lateinit var oxmascarilla: CheckBox
    private lateinit var oxventiladormecanico: CheckBox
    private lateinit var oxotro: CheckBox

    //padece enfermedades respiratorias/pulmonales
    private lateinit var enrSi: CheckBox
    private lateinit var enrNo: CheckBox
    private lateinit var et_enfermedadesRespitatorias: EditText

    //cardiovascular
    private lateinit var crFrecuenciaCardiaca: EditText
    private lateinit var crTA: EditText
    private lateinit var crPVC: EditText





    //valoracion de extremidades inferiores PENDIENTEEEEEEEEEEEEEEEEEEEE
    private lateinit var vxSi: CheckBox
    private lateinit var vxNo: CheckBox
    private lateinit var vxClasificacion: EditText
    private lateinit var vxPulso: EditText
    private lateinit var vxTemp: EditText
    private lateinit var vxColoracion: EditText
    private lateinit var vxLlenadoCapilar: EditText
    private lateinit var vxesi: CheckBox
    private lateinit var vxeno: CheckBox


    // consumo habitual de agua aproximadamente por dia
    private lateinit var chConsumoAgua: EditText

    //origen del consumo de agua
    private lateinit var ocGarrafon: CheckBox
    private lateinit var ocHervida: CheckBox
    private lateinit var ocClorada: CheckBox
    private lateinit var ocPotable: CheckBox
    private lateinit var ocOtroLiquido: EditText
    private lateinit var ocCantidad: EditText

    //presencia de datos de deshidratacion

    private lateinit var pdMucosaOral: CheckBox
    private lateinit var pdCabello: CheckBox
    private lateinit var pdTurgencua: CheckBox

    //consume alcohol

    private lateinit var casi:CheckBox
    private lateinit var cano:CheckBox
    private lateinit var pdEspecificar: EditText
    private lateinit var pdFrecuencia: EditText
    private lateinit var pdCantidad: EditText

    //tipo de producto del alcohol
    private lateinit var tpCerveza: CheckBox
    private lateinit var tpTequila: CheckBox
    private lateinit var tpVinosMesa: CheckBox
    private lateinit var tpPulque: CheckBox
    private lateinit var tpOtroCheckbox: CheckBox
    private lateinit var tpOtro: EditText

    // 3) mantenimiento de un aporte de alimento sificiente

    //somatometria
    private lateinit var soPeso: EditText
    private lateinit var soTalla: EditText
    private lateinit var soImc: EditText
    private lateinit var soCintura: EditText
    private lateinit var soPerimetroAbdominal: EditText
    private lateinit var soNumeroComidaDia: EditText
    private lateinit var soComeFamilia: RadioGroup

    //ingesta de suplementos alimenticios
    private lateinit var isSuplementosSi: RadioButton
    private lateinit var isSuplementosNo: RadioButton
    private lateinit var isCuales: EditText

    //presencia de
    private lateinit var pdnauseas: CheckBox
    private lateinit var pdhematemesis: CheckBox
    private lateinit var pdpolifagia: CheckBox
    private lateinit var pdpirosis: CheckBox
    private lateinit var pdotro: CheckBox
    private lateinit var editpdotro: EditText

    //tabla de consumo de alimentos
    lateinit var cerealesn: CheckBox
    lateinit var cerealescn: CheckBox
    lateinit var cerealesav: CheckBox
    lateinit var cerealescs: CheckBox
    lateinit var cerealess: CheckBox

    lateinit var vegetales_n : CheckBox
    lateinit var vegetales_cn : CheckBox
    lateinit var vegetales_av : CheckBox
    lateinit var vegetales_cs : CheckBox
    lateinit var vegetales_s : CheckBox

    lateinit var frutas_n : CheckBox
    lateinit var frutas_cn : CheckBox
    lateinit var frutas_av : CheckBox
    lateinit var frutas_cs : CheckBox
    lateinit var frutas_s : CheckBox

    lateinit var carnes_n : CheckBox
    lateinit var carnes_cn : CheckBox
    lateinit var carnes_av : CheckBox
    lateinit var carnes_cs : CheckBox
    lateinit var carnes_s : CheckBox

    lateinit var lacteos_n : CheckBox
    lateinit var lacteos_cn : CheckBox
    lateinit var lacteos_av : CheckBox
    lateinit var lacteos_cs : CheckBox
    lateinit var lacteos_s : CheckBox

    lateinit var leguminosas_n : CheckBox
    lateinit var leguminosas_cn : CheckBox
    lateinit var leguminosas_av : CheckBox
    lateinit var leguminosas_cs : CheckBox
    lateinit var leguminosas_s : CheckBox

    //eliminacion vesical
    private lateinit var evNoveces: EditText
    private lateinit var eviSi: RadioButton
    private lateinit var eviNo: RadioButton
    private lateinit var evrSi: RadioButton
    private lateinit var evrNo: RadioButton
    private lateinit var evdSi: RadioButton
    private lateinit var evdNo: RadioButton

    //caracteristicas de la orina
    private lateinit var coEspecificar: EditText
    private lateinit var coCantidad: EditText

    //uso de auxiliares para orina
    private lateinit var uaSi: RadioButton
    private lateinit var uaNo: RadioButton
    private lateinit var uaEspecificar: EditText

    //eliminacion intestinal
    private lateinit var eiFrecuencia: EditText
    private lateinit var eiDolorAbdominal : CheckBox
    private lateinit var eiFlatulencias : CheckBox
    private lateinit var eiEstrenimiento : CheckBox
    private lateinit var eiPujo : CheckBox
    private lateinit var eiDiarrea : CheckBox
    private lateinit var eiMelena : CheckBox
    private lateinit var eiTenesmo : CheckBox
    private lateinit var eiDolorEvacual : CheckBox
    private lateinit var eiHemorroides : CheckBox

    //caracteristica de la evacuacion
    private lateinit var ceEspecificar: EditText

    //uso de auxiliares para evacuar
    private lateinit var aeSi: RadioButton
    private lateinit var aeNo: RadioButton
    private lateinit var ae_Especificar: EditText

    //secreción transvaginal
    private lateinit var stSi : RadioButton
    private lateinit var stNo : RadioButton
    private lateinit var stCaracteristicas : EditText

    //5) MANTENIMIENTO DEL EQULIBRIO ENTRE LA ACTIVIDAD Y EL REPOSO

    //reposo
    private lateinit var reNoHorasNocturno: EditText
    private lateinit var reSi: RadioButton
    private lateinit var reNo: RadioButton
    private lateinit var reNoHoras: EditText

    //¿Tiene dificultades para conciliar el sueño?
    private lateinit var dsSi: RadioButton
    private lateinit var dsNo: RadioButton

    //¿Como se encuentra al despertar?
    private lateinit var eaCansado: CheckBox
    private lateinit var eaDescansado: CheckBox
    private lateinit var eaOtro: CheckBox

    //presencia de:
    private lateinit var prBostezo: CheckBox
    private lateinit var prPesadilla: CheckBox
    private lateinit var prOjeras: CheckBox
    private lateinit var prRonquidos: CheckBox
    private lateinit var prNinguno: CheckBox

    //uso de apoyo para dormir
    private lateinit var adSi: RadioButton
    private lateinit var adNo: RadioButton
    private lateinit var adEspecificar: EditText

    //despierta por la noche
    private lateinit var dnSi: RadioButton
    private lateinit var dnNo: RadioButton
    private lateinit var dnMotivo: EditText

    //actividad: realiza actividad fisica
    private lateinit var afSi: RadioButton
    private lateinit var afNo: RadioButton
    private lateinit var afFrecuencia: EditText
    private lateinit var afTipo: EditText

    //restriccion de actividad fisica
    private lateinit var sdSi: RadioButton
    private lateinit var sdNo: RadioButton

    //necesita apoyo
    private lateinit var naSi: RadioButton
    private lateinit var naNo: RadioButton
    private lateinit var naEspecifique: EditText

    //presencia de dolor en
    private lateinit var rdAbdomen: CheckBox
    private lateinit var rdPiernas: CheckBox
    private lateinit var rdLumbar: CheckBox
    private lateinit var rdCutanea: CheckBox
    private lateinit var rdOtro: CheckBox
    private lateinit var rdElegirOtro: EditText

    //problemas auditivos
    private lateinit var puSi: RadioButton
    private lateinit var puNo: RadioButton
    private lateinit var puEspeficicar: EditText

    //utiliza apoyo para escuchar
    private lateinit var upSi: RadioButton
    private lateinit var upNo: RadioButton

    //presencia de:
    private lateinit var psMareo: CheckBox
    private lateinit var psVertigo: CheckBox
    private lateinit var psAcuferos: CheckBox
    private lateinit var psLesiones: CheckBox
    private lateinit var psOtro: CheckBox

    //sistema ocular problemas y/o alteracion visual
    private lateinit var pvSi: RadioButton
    private lateinit var pvNo: RadioButton
    private lateinit var pvEspecificar: EditText

    //utiliza
    private lateinit var utLentes: CheckBox
    private lateinit var utArmazon: CheckBox
    private lateinit var utContacto: CheckBox
    private lateinit var utTiempoUso: EditText

    //observar presencia de:
    private lateinit var opSeguridad: CheckBox
    private lateinit var opTimidez: CheckBox
    private lateinit var opIntroversion: CheckBox
    private lateinit var opApatia: CheckBox
    private lateinit var opExtroversion: CheckBox
    private lateinit var opAgresividad: CheckBox
    private lateinit var opOtros: CheckBox
    private lateinit var opElegirOtro: EditText

    //la mayor parte del tiempo la pasa:
    private lateinit var lpEnCasa: CheckBox
    private lateinit var lpConFamilia: CheckBox
    private lateinit var lpTrabajo: CheckBox
    private lateinit var lpAmistades: CheckBox

    //realiza actividades recreativas
    private lateinit var arSi: RadioButton
    private lateinit var arNo: RadioButton
    private lateinit var arEspeficicar: EditText

    //como considera sus relaciones
    private lateinit var saBuena: CheckBox
    private lateinit var saRegular: CheckBox
    private lateinit var saMala : CheckBox

    //como es la relacion con el personal de salud
    private lateinit var prBuena: CheckBox
    private lateinit var prRegular: CheckBox
    private lateinit var prMala : CheckBox

    //7) Prevención de peligros para la vida, el funcionamiento y bienestar humano
    //tipo y RH sanguineo
    private lateinit var tiEspecifiquerh: EditText

    //presencia de heridas
    private lateinit var phSi : RadioButton
    private lateinit var phNo : RadioButton
    private lateinit var phTipo: EditText
    private lateinit var phCaracteristica: EditText

    //caracteristicas del acceso venoso
    private lateinit var veCaracteristicas: EditText

    //cateterismo vesical
    private lateinit var cvSi: RadioButton
    private lateinit var cvNo: RadioButton
    private lateinit var cvOtros: EditText

    //PROMOCION DEL FUNCIONAMIENTO HUMANO
    //aceptacion de su aspecto fisico

    private lateinit var aiSi: RadioButton
    private lateinit var aiNo: RadioButton

    //pertecenece a algun grupo social
    private lateinit var grSi: RadioButton
    private lateinit var grNo: RadioButton

    //Aceptacion de los cambios a partir de su enfermedad
    private lateinit var efSi: RadioButton
    private lateinit var efNo: RadioButton

    //presencia de alteraciones emocionales
    private lateinit var erDepresion: CheckBox
    private lateinit var erAnsiedad: CheckBox
    private lateinit var erVerguenza: CheckBox
    private lateinit var erTemor: CheckBox
    private lateinit var erDesesperanza: CheckBox
    private lateinit var erOtro: CheckBox
    private lateinit var erOtroEspecifique: EditText

    //B DE DESARROLLO
    // Los que apoyan los procesos vitales
    private lateinit var eivs: EditText

    //VSA
    private lateinit var vsaSi: RadioButton
    private lateinit var vsaNo: RadioButton

    //se protegio en su ultima relacion sexual
    private lateinit var ulSi: RadioButton
    private lateinit var ulNo: RadioButton

    //no de parejas sexuales
    private lateinit var noParejas: EditText

    //conforme con sus preferencias sexuales
    private lateinit var rsSi: RadioButton
    private lateinit var rsNo: RadioButton

    //considera las relaciones sexuales
    private lateinit var leNecesarias: CheckBox
    private lateinit var lePlacenteras: CheckBox
    private lateinit var leIncomodas: CheckBox
    private lateinit var leForzosas: CheckBox
    private lateinit var leAgradables: CheckBox
    private lateinit var leExcitantes: CheckBox
    private lateinit var leDolorosas: CheckBox
    private lateinit var leOtro: CheckBox
    private lateinit var leEspecifique: EditText

    //esquema de vacunacion segun edad
    private lateinit var svCompleto: CheckBox
    private lateinit var svIncompleto: CheckBox
    private lateinit var svEspecificar: EditText

    //mujer edad de
    private lateinit var edMenarca: EditText
    private lateinit var edTelarca: EditText
    private lateinit var edPubarca: EditText
    private lateinit var edMenopausia: EditText

    //caracteristicas de la menstruacion
    private lateinit var mrRegular: RadioButton
    private lateinit var mrIrregular: RadioButton
    private lateinit var mrDuracion: EditText

    //metodo de planificacion utilizados
    private lateinit var poPlanificacion: EditText

    //FUM
    private lateinit var mFum: TextView

    private lateinit var mgGestas: EditText
    private lateinit var mgPara: EditText
    private lateinit var mgAbortos: EditText
    private lateinit var mgCesareas: EditText
    private lateinit var mgNacidosVivos: EditText

    //embarazo de alto riesgo
    private lateinit var ebSi: RadioButton
    private lateinit var ebNo: RadioButton

    //cirugia ap. reproductor
    private lateinit var ebcSi: RadioButton
    private lateinit var ebcNo: RadioButton

    private lateinit var ebEspecifique: EditText

    //fecha del ultimo papanicolau
    private lateinit var pfPapanicolau: TextView
    private lateinit var pfResultado: EditText

    //exploracion de mama
    private lateinit var exExploracionmama: TextView
    private lateinit var exResultado: EditText


    //hombre ultimo examen de prostata
    private lateinit var exhProstata: TextView
    private lateinit var exhEspecifique: EditText
    private lateinit var exhResultado: EditText

    //alteracion en aparato reproductor
    private lateinit var atSi: RadioButton
    private lateinit var atNo: RadioButton
    private lateinit var atEspecificar: EditText

    //asistencias a campañas de salud
    private lateinit var cwSi: RadioButton
    private lateinit var cwNo: RadioButton

    //como responde ante situaciones de duelo o perdida
    private lateinit var fcSituaciones: EditText

    //cambio de residente
    private lateinit var rwSi: RadioButton
    private lateinit var rwNo: RadioButton

    //casa
    private lateinit var cwcSi: RadioButton
    private lateinit var cwcNo: RadioButton

    //trabajo
    private lateinit var twcSi: RadioButton
    private lateinit var twcNo: RadioButton

    //familia
    private lateinit var fwcSi: RadioButton
    private lateinit var fwcNo: RadioButton

    //ingresos
    private lateinit var iwcSi: RadioButton
    private lateinit var iwcNo: RadioButton

    //4  Mala salud, condiciones de vida o incapacidad, enfermedad terminal
    // se enferma con frecuencia
    private lateinit var enfSi: RadioButton
    private lateinit var enfNo: RadioButton

    //presenta problemas psicologicos
    private lateinit var pscSi: RadioButton
    private lateinit var pscNo: RadioButton

    //es autosufuciente
    private lateinit var utsSi: RadioButton
    private lateinit var utsNo: RadioButton

    //5. peligros ambientales tiene contacto con:
    private lateinit var tccPesticidas: CheckBox
    private lateinit var tccBioxidoCarbono: CheckBox
    private lateinit var tccZonaInsalubre: CheckBox

    //es adicto
    private lateinit var tccaSi: RadioButton
    private lateinit var tccaNo: RadioButton


    //convive con adictos
    private lateinit var tccSi: RadioButton
    private lateinit var tccNo: RadioButton
    private lateinit var tccEspecificar: EditText

    //adiccion entorno
    private lateinit var tcceSi: RadioButton
    private lateinit var tcceNo: RadioButton

    //cruza la calle con precaucion
    private lateinit var crpSi: RadioButton
    private lateinit var crpNo: RadioButton

    //utiliza cinturon de seguridad
    private lateinit var utlSi: RadioButton
    private lateinit var utlNo: RadioButton

    //cierre las puertas con llave
    private lateinit var lavSi: RadioButton
    private lateinit var lavNo: RadioButton

    // cierra el tanque de gas
    private lateinit var cgaSi: RadioButton
    private lateinit var cgaNo: RadioButton

    //c) desviacion de la salud
    //requiere de cuidados especificso preventivos y regulares
    private lateinit var rpSi: RadioButton
    private lateinit var rpNo: RadioButton






    //-----------------> boton de guardar
    private lateinit var btnGuardar: Button



    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seccion1_activity_guia_valoracion)

        supportActionBar?.title = "Sección A - Descriptivos"

        etIdEstudiante = findViewById(R.id.et_Id)
        etNombre = findViewById(R.id.et_nombre)
        etEdad = findViewById(R.id.et_edad)
        rgSexo = findViewById(R.id.rg_sexo)
        religion = findViewById(R.id.et_religion)
        escolaridad = findViewById(R.id.et_escolaridad)
        ocupacion = findViewById(R.id.et_ocupacion)
        estadoCivil = findViewById(R.id.et_EstadoCivil)
        tvFechaIngreso = findViewById(R.id.tv_fecha_ingreso)
        rgPiso = findViewById(R.id.rg_piso)
        noVentanas = findViewById((R.id.rg_ventanas))




        cbBloque = findViewById(R.id.cb_bloque)
        cbLadrillo = findViewById(R.id.cb_ladrillo)
        cbAdobe = findViewById(R.id.cb_adobe)
        cbLamina = findViewById(R.id.cb_lamina)
        cbMadera = findViewById(R.id.cb_madera)
        cbParedVarias = findViewById(R.id.cb_pared_varias)

        ///techo
        tcConcreto = findViewById(R.id.tc_concreto)
        tcLamina = findViewById(R.id.tc_lamina)
        tcTeja = findViewById(R.id.tc_teja)
        tcCarton = findViewById(R.id.tc_carton)

        luz = findViewById(R.id.rg_luz)

        //agua
        cbLlave = findViewById(R.id.cb_llave)
        cbPozo = findViewById(R.id.cb_pozo)
        cbPipa = findViewById(R.id.cb_pipa)
        cbOtroAgua = findViewById(R.id.cb_otro_agua)
        etAguaOtro = findViewById(R.id.et_agua_otro)

        cbOtroAgua.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                etAguaOtro.isEnabled= true
            }else{
                etAguaOtro.isEnabled= false
                etAguaOtro.text.clear()
            }
        }




        val checkboxAgua = listOf(cbLlave, cbPozo, cbPipa)

        setupExclusiveCheckBox(cbOtroAgua,etAguaOtro,checkboxAgua)

        //purificacionAgua
        meClorada = findViewById(R.id.me_clorada)
        meHervida = findViewById(R.id.me_hervida)
        meNinguno = findViewById(R.id.me_ninguno)
        meGarrafon = findViewById(R.id.me_garrafon)
        meMasDeUno = findViewById(R.id.me_masdeuno)

        //Drenaje y Alcantarillado
        dcDrenaje = findViewById(R.id.dc_drenaje)
        dcLetrina = findViewById(R.id.dc_letrina)
        dcFosa = findViewById(R.id.dc_fosa)
        dcSuelo = findViewById(R.id.dc_raSuelo)

        //basura
        trCamion = findViewById(R.id.tr_camion)
        trQuema = findViewById(R.id.tr_quema)
        trEnterrar = findViewById(R.id.tr_enterrar)
        trMasDeUno = findViewById(R.id.tr_masdeuno)
        trNinguno = findViewById(R.id.tr_ninguno)

        //fauna nosiva
        faRatones = findViewById(R.id.fa_ratones)
        faMoscas = findViewById(R.id.fa_moscas)
        faMosquitos = findViewById(R.id.fa_mosquitos)
        faArañas = findViewById(R.id.fa_arañas)
        faCucarachas = findViewById(R.id.fa_cucarachas)
        faPulgas = findViewById(R.id.fa_pulgas)
        faMasDeUno = findViewById(R.id.fa_masdeuno)
        faNinguno = findViewById(R.id.fa_ninguno)

        //animales domesticos
        anPerros = findViewById(R.id.an_perros)
        anGatos = findViewById(R.id.an_gatos)
        anAves = findViewById(R.id.an_aves)
        anCerdos = findViewById(R.id.an_cerdos)
        anMasDeUno = findViewById(R.id.an_masdeuno)
        anOtro = findViewById(R.id.an_otro)
        anNinguno = findViewById(R.id.an_ninguno)

        //No. de animales domesticos
        noAnimalesDomesticos = findViewById(R.id.do_domesticos)

        //Animales vacunados
        animalesVacunados = findViewById(R.id.va_vacunados)


        /////////////////////  SECCION B

        //relacion familiar
        relacionFamiliar = findViewById(R.id.rf_relacion)

        //ingreso economico familiar
        ie1sm = findViewById(R.id.ie_1sm)
        ie12sm = findViewById(R.id.ie_12sm)
        ie3sm = findViewById(R.id.ie_3sm)
        ieninguno = findViewById(R.id.ie_ninguno)

        //dependencia economica
        dependenciaEconomica = findViewById(R.id.de_economica)

        //estado nutricional
        nuDesnutricion = findViewById(R.id.nu_desnutricion)
        nuNormal = findViewById(R.id.nu_normal)
        nuSobrepeso = findViewById(R.id.nu_sobrepeso)
        nuObesidad = findViewById(R.id.nu_obesidad)

        //cabello
        caquebradizo = findViewById(R.id.ca_quebradizo)
        caopaco = findViewById(R.id.ca_opaco)
        caescaso = findViewById(R.id.ca_escaso)
        camasdeuno = findViewById(R.id.ca_masdeuno)
        casaludable = findViewById(R.id.ca_saludable)

        //mucosa
        muSangrado = findViewById(R.id.mu_sangrado)
        muInfeccion = findViewById(R.id.mu_infeccion)
        muPalidez = findViewById(R.id.mu_palidez)
        muSaludable = findViewById(R.id.mu_saludable)

        //piel
        piPielGallina = findViewById(R.id.pi_gallina)
        piSecaQuebrada = findViewById(R.id.pi_secaquebrada)
        piPuntosNegros = findViewById(R.id.pi_puntosnegros)
        piSecaMaltratada = findViewById(R.id.pi_secamaltratada)
        piEscamosa = findViewById(R.id.pi_escamosa)
        piPalidez = findViewById(R.id.pi_palidez)
        pimasdeuno = findViewById(R.id.pi_masdeuno)
        piSaludable = findViewById(R.id.pi_saludable)

        //labios
        laLesiones = findViewById(R.id.la_lesiones)
        laSecos = findViewById(R.id.la_secos)
        laAgrietados = findViewById(R.id.la_agrietados)
        laMasdeuno = findViewById(R.id.la_masdeuno)
        laSaludables = findViewById(R.id.la_saludables)

        //encias
        enHinchadas = findViewById(R.id.en_hinchadas)
        enSangran = findViewById(R.id.en_sangran)
        enMasdeUno = findViewById(R.id.en_masdeuno)
        enSaludable = findViewById(R.id.en_saludable)

        //nariz y orejas
        orLesiones = findViewById(R.id.or_conlesiones)
        orHumedas = findViewById(R.id.or_humedas)
        orEnrojecidas = findViewById(R.id.or_enrojecidas)
        orMasdeuno = findViewById(R.id.or_masdeuno)
        orSaludables = findViewById(R.id.or_saludables)

        //uñas
        asPalidas = findViewById(R.id.as_palidas)
        asQuebradas = findViewById(R.id.as_quebradas)
        asEstriadas = findViewById(R.id.as_estriadas)
        asMasdeuno = findViewById(R.id.as_masdeuno)
        asSaludables = findViewById(R.id.as_saludables)

        //sistema oseo
        soEncorvado = findViewById(R.id.so_encorvado)
        soReduccion = findViewById(R.id.so_reduccion)
        soDecalcificacion = findViewById(R.id.so_descalcificacion)
        soMasdeuno = findViewById(R.id.so_masdeuno)
        soSaludable = findViewById(R.id.so_saludable)

        //en general
        geDebilidad = findViewById(R.id.ge_debilidad)
        geFatiga = findViewById(R.id.ge_fatiga)
        geCansancio = findViewById(R.id.ge_cansancio)
        geCianosis = findViewById(R.id.ge_cianosis)
        geMasdeuno = findViewById(R.id.ge_masdeuno)
        geSaludable = findViewById(R.id.ge_saludable)

        //variacion ultimos seis meses
        vpPeso = findViewById(R.id.vp_peso)

        //kilos subido
        kisubido = findViewById(R.id.ki_subido)

        //kilos perdidos
        kpPerdido = findViewById(R.id.kp_perdido)

        //dentadura
        cdCompleta = findViewById(R.id.cd_completa)
        cdFrenos = findViewById(R.id.cd_frenos)
        cdProtesis = findViewById(R.id.cd_protesis)
        cdMaloclusion = findViewById(R.id.cd_maloclusion)
        cdMasdeuno = findViewById(R.id.cd_masdeuno)

        //problema cavidad oral
        coSi = findViewById(R.id.co_si)
        coNo = findViewById(R.id.co_no)
        etCavidadDetalle = findViewById(R.id.et_cavidad_detalle)


        coSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                coNo.isChecked= false
                etCavidadDetalle.isEnabled= true
            }

        }

        coNo.setOnCheckedChangeListener {  _, isChecked ->
            if(isChecked){
                coSi.isChecked = false
                etCavidadDetalle.isEnabled= false
                etCavidadDetalle.text.clear()
            }


        }


        //problemas dentales
        deSi = findViewById(R.id.de_si)
        deNo = findViewById(R.id.de_no)
        etProblemasDentales = findViewById(R.id.et_problemasDentales)

        deSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                deNo.isChecked= false
                etProblemasDentales.isEnabled= true
            }

        }

        deNo.setOnCheckedChangeListener {  _, isChecked ->
            if(isChecked){
                deSi.isChecked = false
                etProblemasDentales.isEnabled= false
                etProblemasDentales.text.clear()
            }
        }

        //dijerirAlimentos
        daSi = findViewById(R.id.da_si)
        daNo = findViewById(R.id.da_no)
        et_dijerirAlimentos = findViewById(R.id.et_dijerirAlimentos)

        daSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                daNo.isChecked= false
                et_dijerirAlimentos.isEnabled= true
            }

        }

        daNo.setOnCheckedChangeListener {  _, isChecked ->
            if(isChecked){
                daSi.isChecked = false
                et_dijerirAlimentos.isEnabled= false
                et_dijerirAlimentos.text.clear()
            }


        }

        //alimentos que no pueden comer
        alderivadosLeche = findViewById(R.id.al_derivadosLeche)
        alcarnes = findViewById(R.id.al_carnes)
        alazucares = findViewById(R.id.al_azucares)
        algrasas = findViewById(R.id.al_grasas)
        alsales = findViewById(R.id.al_sales)
        alcolesterol = findViewById(R.id.al_colesterol)
        almasdeuno = findViewById(R.id.al_masuno)
        alotros = findViewById(R.id.al_otro)
        alcometodo = findViewById(R.id.al_comeTodo)


        //con que guisa sus alimentos
        agaceiteVegetal = findViewById(R.id.ag_aceteVegetal)
        agaceiteAnimal = findViewById(R.id.ag_aceiteAnimal)
        agmanteca = findViewById(R.id.ag_manteca)


        //come desayuno
        cmlechecafe = findViewById(R.id.cm_lechecafe)
        cmpancereal = findViewById(R.id.cm_pancereal)
        cmfrutatamales = findViewById(R.id.cm_frutatamales)
        cmhuevoquesadillas = findViewById(R.id.cm_huevoquesadillas)
        cmrefrescojugo = findViewById(R.id.cm_refrescofrutas)


        //come comida
        ccrefrecoagua = findViewById(R.id.cc_refrescoagua)
        ccverdurastacos = findViewById(R.id.cc_verdurastacos)
        ccsopaarroz = findViewById(R.id.cc_sopaarroz)
        cccarnesoya = findViewById(R.id.cc_carnesoya)
        ccfrijolesgorditas = findViewById(R.id.cc_frijolesgorditas)

        //come en la cena
        qclechecafe = findViewById(R.id.qc_lechecafe)
        qctacosfruta = findViewById(R.id.qc_tacosfruta)
        qcyogurthtacos = findViewById(R.id.qc_yogurttacos)

        //cepillado de dientes
        cddiario = findViewById(R.id.cd_diario)
        cddosdias = findViewById(R.id.cd_dosdias)
        cdtresdias = findViewById(R.id.cd_tresdias)
        cdcuatrodias = findViewById(R.id.cd_cuatrodias)
        cdesporadicamente = findViewById(R.id.cd_esporadicamente)
        cdnunca = findViewById(R.id.cd_nunca)

        //se baña
        sbdiario = findViewById(R.id.sb_diario)
        sbdosdias = findViewById(R.id.sb_dosdias)
        sbtresdias = findViewById(R.id.sb_tresdias)

        //cambio de ropa
        cpdiario = findViewById(R.id.cp_diario)
        cpdosdias = findViewById(R.id.cp_dosdias)
        cptresdias = findViewById(R.id.cp_tresdias)

        //lavadoManos
        lmantescomer = findViewById(R.id.lm_antescomer)
        lmdespuesbano = findViewById(R.id.lm_despuesbano)
        lmantespreparar = findViewById(R.id.lm_antespreparar)
        lmmasdeunavez = findViewById(R.id.lm_masunavez)
        lmaveces = findViewById(R.id.lm_aveces)

        //////////////////// SECCION C

        //servicio de salud
        seimss = findViewById(R.id.se_imss)
        seissste = findViewById(R.id.se_issste)
        seisea = findViewById(R.id.se_isea)
        separticular = findViewById(R.id.se_particular)
        seseguropopular = findViewById(R.id.se_seguroPopular)
        seotro = findViewById(R.id.se_otro)
        etServicioSocial = findViewById(R.id.se_especificar)

        seotro.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                etServicioSocial.isEnabled= true
            }else{
                etServicioSocial.isEnabled = false
                etServicioSocial.text.clear()
            }
        }
        val checkboxSalud = listOf(seimss,seissste,seisea,separticular,seseguropopular)
        setupExclusiveCheckBox(seotro,etServicioSocial,checkboxSalud)




        //cuando acude al servicio medico RADIOBUTHON
        acServicioMedico = findViewById(R.id.ac_serviciomedico)

        //capaz de tomar decisiones respecto a su tratamiento
        drNo = findViewById(R.id.dr_no)
        drSi = findViewById(R.id.dr_si)
        etDecisiones = findViewById(R.id.et_decisiones)

        drSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                drNo.isChecked= false
                etDecisiones.isEnabled= false
                etDecisiones.text.clear()
            }

        }

        drNo.setOnCheckedChangeListener {  _, isChecked ->
            if(isChecked){
                drSi.isChecked = false
                etDecisiones.isEnabled= true

            }


        }

        //presencia de alguna enfermedad
        peNo = findViewById(R.id.pe_no)
        peSi = findViewById(R.id.pe_si)
        etPresenciaEnfermedad = findViewById(R.id.et_presenciaenfermedad)

        peSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                peNo.isChecked= false
                etPresenciaEnfermedad.isEnabled= true
            }

        }

        peNo.setOnCheckedChangeListener {  _, isChecked ->
            if(isChecked){
                peSi.isChecked = false
                etPresenciaEnfermedad.isEnabled= false
                etPresenciaEnfermedad.text.clear()
            }


        }



        //tiene tratamiento
        trNo = findViewById(R.id.tr_no)
        trSi = findViewById(R.id.tr_si)
        et_tratamiento = findViewById(R.id.et_tratamiento)

        trSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                trNo.isChecked= false
                et_tratamiento.isEnabled= true
            }

        }

        trNo.setOnCheckedChangeListener {  _, isChecked ->
            if(isChecked){
                trSi.isChecked = false
                et_tratamiento.isEnabled= false
                et_tratamiento.text.clear()
            }


        }

        //lleva a cabo su tratamiento
        ctNo = findViewById(R.id.ct_no)
        ctSi = findViewById(R.id.ct_si)

        ctSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ctNo.isChecked = false   // desmarca "No"
            }
        }

        ctNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ctSi.isChecked = false   // desmarca "Sí"

            }
        }




        //PARTE II MANTENIMENTO DE UN APORTE DE AIRE SUFICIENTE

        //frecuencia respiratoria
        frrespiratorias = findViewById(R.id.fr_frecuenciaRespiratoria)


        //cianosis
        ciaSi = findViewById(R.id.cia_si)
        ciaNo = findViewById(R.id.cia_no)
        ciaEspecifique = findViewById(R.id.cia_especifique)


        ciaSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ciaNo.isChecked = false   // desmarca "No"
                ciaEspecifique.isEnabled = true    // habilita el EditText
            }
        }

        ciaNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ciaSi.isChecked = false   // desmarca "Sí"
                ciaEspecifique.isEnabled = false   // desactiva EditText
                ciaEspecifique.text.clear()        // limpia el campo
            }
        }


        //llenado capilar
        lcSi = findViewById(R.id.lc_si)
        lcNo= findViewById(R.id.lc_no)

        lcSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lcNo.isChecked = false   // desmarca "No"
            }
        }

        lcNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lcSi.isChecked = false   // desmarca "Sí"
            }
        }

        //fuma
        fuSi = findViewById(R.id.fu_si)
        fuNo = findViewById(R.id.fu_no)

        fuSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fuNo.isChecked = false   // desmarca "No"
            }
        }

        fuNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fuSi.isChecked = false   // desmarca "Sí"
            }
        }



        //no.cigarrillos al dia
        csCigarrillos = findViewById(R.id.cs_cigarrillosDia)

        //convive con fumadores
        cnSi = findViewById(R.id.cn_si)
        cnNo = findViewById(R.id.cn_no)

        cnSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cnNo.isChecked = false   // desmarca "No"
            }
        }

        cnNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cnSi.isChecked = false   // desmarca "Sí"

            }
        }




        //tos frecuente
        tfNo = findViewById(R.id.tf_no)
        tfSi = findViewById(R.id.tf_si)

        //ruidos respiratorios anormales
        raNo = findViewById(R.id.ra_no)
        raSi = findViewById(R.id.ra_si)


        //Factores desencadenantes de alteraciones respiratorias
        etFactoresAlteracion = findViewById(R.id.et_factoresAlteracion)

        //Oxigenoterapia
        oxpuntasnasales = findViewById(R.id.ox_puntasnasales)
        oxmascarilla = findViewById(R.id.ox_mascarilla)
        oxventiladormecanico = findViewById(R.id.ox_ventiladorMecanico)
        oxotro = findViewById(R.id.ox_otro)


        //padece de enfermedades respiratorias/pulmonales
        enrNo = findViewById(R.id.enr_no)
        enrSi = findViewById(R.id.enr_si)
        et_enfermedadesRespitatorias = findViewById(R.id.et_enfermedadesRespiratorias)

        enrSi.setOnCheckedChangeListener { _, isChecked ->
            et_enfermedadesRespitatorias.isEnabled = isChecked
            if (!isChecked) {
                et_enfermedadesRespitatorias.setText("")
            }
        }

        //cardiovascular
        crFrecuenciaCardiaca = findViewById(R.id.cr_frecuenciaCardiaca)
        crTA = findViewById(R.id.cr_TA)
        crPVC = findViewById(R.id.cr_PVC)



        //valoracion de extremidades inferiores ME QUEDE AQUIIIIIIIIIIIIIIII

        vxSi = findViewById(R.id.vx_si)
        vxNo = findViewById(R.id.vx_no)

        vxSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vxNo.isChecked = false   // desmarca "No"
            }
        }

        vxNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vxSi.isChecked = false   // desmarca "Sí"

            }
        }

        vxClasificacion=findViewById(R.id.vx_clasificacion)
        vxPulso=findViewById(R.id.vx_pulso)
        vxTemp=findViewById(R.id.vx_temp)
        vxColoracion=findViewById(R.id.vx_coloracion)
        vxLlenadoCapilar=findViewById(R.id.vx_llenadoCapilar)


        //edema
        vxesi=findViewById(R.id.vxe_si)
        vxeno=findViewById(R.id.vxe_no)


        vxesi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vxeno.isChecked = false   // desmarca "No"
            }
        }

        vxeno.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vxesi.isChecked = false   // desmarca "Sí"

            }
        }






        // Mantenimiento de un aporte de agua suficiente

        //consumo habitual de agua aproximadamente por dia
        chConsumoAgua = findViewById(R.id.ch_consumoAgua)


        //origen de consumo de agua
        ocGarrafon = findViewById(R.id.oc_garrafon)
        ocHervida = findViewById(R.id.oc_hervida)
        ocClorada = findViewById(R.id.oc_clorada)
        ocPotable = findViewById(R.id.oc_potable)
        ocOtroLiquido = findViewById(R.id.oc_otroLiquido)
        ocCantidad = findViewById(R.id.oc_cantidad)


        //presencia de datos deshidratados

        pdMucosaOral = findViewById(R.id.pd_mucosaOral)
        pdCabello = findViewById(R.id.pd_cabello)
        pdTurgencua = findViewById(R.id.pd_turgencia)

        //consumo de alcohol
        casi = findViewById(R.id.ca_si)
        cano = findViewById(R.id.ca_no)
        pdEspecificar = findViewById(R.id.pd_especifique)
        pdFrecuencia = findViewById(R.id.pd_frecuencia)
        pdCantidad = findViewById(R.id.pd_cantidad)

        casi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                pdEspecificar.isEnabled = true
                pdFrecuencia.isEnabled = true
                pdCantidad.isEnabled = true
            }
        }

        cano.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                pdEspecificar.isEnabled = false
                pdFrecuencia.isEnabled = false
                pdCantidad.isEnabled = false
                pdEspecificar.text.clear()
                pdFrecuencia.text.clear()
                pdCantidad.text.clear()
            }
        }

        //tipo de producto del alcohol
        tpCerveza = findViewById(R.id.tp_cerveza)
        tpTequila = findViewById(R.id.tp_tequila)
        tpVinosMesa = findViewById(R.id.tp_vinosMesa)
        tpPulque = findViewById(R.id.tp_pulque)
        tpOtroCheckbox = findViewById(R.id.tp_otrocheckbox)
        tpOtro = findViewById(R.id.tp_otro)

        tpOtroCheckbox.setOnCheckedChangeListener { _, isChecked ->
            tpOtro.isEnabled = isChecked
            if (!isChecked) {
                tpOtro.setText("")
            }

        }



        //////////////////// 3) MANTENIMIENTO DE UN APORTE DE ALIMENTO SIFICIENTE

        // Somatometria
        soPeso = findViewById(R.id.so_peso)
        soTalla = findViewById(R.id.so_talla)
        soImc = findViewById(R.id.so_imc)
        soCintura = findViewById(R.id.so_cintura)
        soPerimetroAbdominal = findViewById(R.id.so_perimetroabdominal)
        soNumeroComidaDia = findViewById(R.id.so_noComida)
        soComeFamilia = findViewById(R.id.so_comeFamilia)

        //ingesta de suplementos alimenticios
        isSuplementosSi = findViewById(R.id.is_si)
        isSuplementosNo = findViewById(R.id.is_no)
        isCuales = findViewById(R.id.is_cuales)

        isSuplementosSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isCuales.isEnabled = true
            }
        }

        isSuplementosNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isCuales.isEnabled = false
                isCuales.text.clear()
            }
        }

        // presencia de
        pdnauseas = findViewById(R.id.pd_nauseas)
        pdhematemesis = findViewById(R.id.pd_Hematemesis)
        pdpolifagia = findViewById(R.id.pd_polifagia)
        pdpirosis = findViewById(R.id.pd_pirosis)
        pdotro = findViewById(R.id.pd_otro)

        editpdotro = findViewById(R.id.editpd_otro)


        pdotro.setOnCheckedChangeListener { _, isChecked ->
            editpdotro.isEnabled = isChecked
            if (!isChecked) {
                editpdotro.setText("")
            }
        }


        //tabla de la frecuencia de alimentos que se consume

        //cereales
        cerealesn = findViewById(R.id.CerealesNunca)
        cerealescn = findViewById(R.id.CerealesCN)
        cerealesav = findViewById(R.id.CerealesAV)
        cerealescs = findViewById(R.id.CerealesCS)
        cerealess = findViewById(R.id.CerealesS)

        setupSingleSelection(cerealesn, cerealescn, cerealesav, cerealescs, cerealess)

        vegetales_n = findViewById(R.id.Vegetales_N)
        vegetales_cn = findViewById(R.id.Vegetales_CN)
        vegetales_av = findViewById(R.id.Vegetales_AV)
        vegetales_cs = findViewById(R.id.Vegetales_CS)
        vegetales_s = findViewById(R.id.Vegetales_S)

        setupSingleSelection(vegetales_n, vegetales_cn, vegetales_av, vegetales_cs, vegetales_s)

        frutas_n = findViewById(R.id.frutas_N)
        frutas_cn = findViewById(R.id.frutas_CN)
        frutas_av = findViewById(R.id.frutas_AV)
        frutas_cs = findViewById(R.id.frutas_CS)
        frutas_s = findViewById(R.id.frutas_S)

        setupSingleSelection(frutas_n, frutas_cn, frutas_av, frutas_cn, frutas_s)

        carnes_n = findViewById(R.id.carnes_N)
        carnes_cn = findViewById(R.id.carnes_CN)
        carnes_av = findViewById(R.id.carnes_AV)
        carnes_cs = findViewById(R.id.carnes_CS)
        carnes_s = findViewById(R.id.carnes_S)

        setupSingleSelection(carnes_n, carnes_cn, carnes_av, carnes_cs, carnes_s)

        lacteos_n = findViewById(R.id.lacteos_N)
        lacteos_cn = findViewById(R.id.lacteos_CN)
        lacteos_av = findViewById(R.id.lacteos_AV)
        lacteos_cs = findViewById(R.id.lacteos_CS)
        lacteos_s = findViewById(R.id.lacteos_S)

        setupSingleSelection(lacteos_n, lacteos_cn, lacteos_av, lacteos_cs, lacteos_s)

        leguminosas_n = findViewById(R.id.leguminosas_N)
        leguminosas_cn = findViewById(R.id.leguminosas_CN)
        leguminosas_av = findViewById(R.id.leguminosas_AV)
        leguminosas_cs = findViewById(R.id.leguminosas_CS)
        leguminosas_s = findViewById(R.id.leguminosas_S)

        setupSingleSelection(
            leguminosas_n,
            leguminosas_cn,
            leguminosas_av,
            leguminosas_cs,
            leguminosas_s
        )


        //eliminacion vesical
        evNoveces = findViewById(R.id.ev_no_veces)
        eviSi = findViewById(R.id.evi_si)
        eviNo = findViewById(R.id.evi_no)
        evrSi = findViewById(R.id.evr_si)
        evrNo = findViewById(R.id.evr_no)
        evdSi = findViewById(R.id.evd_si)
        evdNo = findViewById(R.id.evd_no)

        //caracteristicas de la orina
        coEspecificar = findViewById(R.id.co_especificar)
        coCantidad = findViewById(R.id.co_cantidad)

        //uso de auxiliares para orina
        uaSi = findViewById(R.id.ua_si)
        uaNo = findViewById(R.id.ua_no)
        uaEspecificar = findViewById(R.id.ua_especificar)

        uaSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uaEspecificar.isEnabled = true
            }

        }

        uaNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uaEspecificar.isEnabled = false
                uaEspecificar.text.clear()
            }
        }

        //eliminacion intestinal
        eiFrecuencia = findViewById(R.id.ei_frecuencia)
        eiDolorAbdominal = findViewById(R.id.ei_dolorAbdominal)
        eiFlatulencias = findViewById(R.id.ei_flatulencias)
        eiEstrenimiento = findViewById(R.id.ei_estrenimiento)
        eiPujo = findViewById(R.id.ei_pujo)
        eiDiarrea = findViewById(R.id.ei_diarrea)
        eiMelena = findViewById(R.id.ei_melena)
        eiTenesmo = findViewById(R.id.ei_tenesmo)
        eiDolorEvacual = findViewById(R.id.ei_dolorEvacual)
        eiHemorroides = findViewById(R.id.ei_hemorroides)

        //caracteristica de la evacuacion
        ceEspecificar = findViewById(R.id.ce_especificar)

        //uso de auxiliares para evacuar
        aeSi = findViewById(R.id.ae_si)
        aeNo = findViewById(R.id.ae_no)
        ae_Especificar= findViewById(R.id.ae_especificar)


        aeSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                ae_Especificar.isEnabled= true
            }
        }

        aeNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                ae_Especificar.isEnabled= false
                ae_Especificar.text.clear()
            }
        }

        //secreción transvaginal
        stSi = findViewById(R.id.st_si)
        stNo = findViewById(R.id.st_no)
        stCaracteristicas = findViewById(R.id.st_caracteristicas)

        stSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                stCaracteristicas.isEnabled= true
            }

        }

        stNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                stCaracteristicas.isEnabled= false
                stCaracteristicas.text.clear()
            }

        }

        //5) MANTENIMIENTO DEL EQUILIBRIO ENTRE LA ACTIVIDAD Y EL REPOSO
        //reposo
        reNoHorasNocturno = findViewById(R.id.re_noHorasNocturno)

        //realiza siestas diurnas
        reSi = findViewById(R.id.re_si)
        reNo = findViewById(R.id.re_no)
        reNoHoras = findViewById(R.id.re_noHoras)

        reSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                reNoHoras.isEnabled= true
            }
        }

        reNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                reNoHoras.isEnabled= false
                reNoHoras.text.clear()
            }
        }

        //¿Tiene dificultad para conciliar el sueño?
        dsSi = findViewById(R.id.ds_si)
        dsNo = findViewById(R.id.ds_no)

        //¿Como se encuentra al despertar?
        eaCansado = findViewById(R.id.ea_cansando)
        eaDescansado = findViewById(R.id.ea_descansado)
        eaOtro = findViewById(R.id.ea_otro)

        //presencia de:
        prBostezo = findViewById(R.id.pr_bostezo)
        prPesadilla = findViewById(R.id.pr_pesadilla)
        prOjeras= findViewById(R.id.pr_ojeras)
        prRonquidos = findViewById(R.id.pr_ronquidos)
        prNinguno = findViewById(R.id.pr_ninguno)

        //uso de apoyos para dormir
        adSi = findViewById(R.id.ad_si)
        adNo = findViewById(R.id.ad_no)
        adEspecificar = findViewById(R.id.ad_especificar)

        adSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                adEspecificar.isEnabled= true
            }

        }

        adNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                adEspecificar.isEnabled= false
                adEspecificar.text.clear()
            }

        }

        //despierta por la noche
        dnSi = findViewById(R.id.dn_si)
        dnNo = findViewById(R.id.dn_no)
        dnMotivo= findViewById(R.id.dn_motivo)

        dnSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                dnMotivo.isEnabled= true
            }
        }

        dnNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                dnMotivo.isEnabled= false
                dnMotivo.text.clear()
            }

        }


        //actividad: realiza actividad fisica
        afSi = findViewById(R.id.af_si)
        afNo = findViewById(R.id.af_no)
        afFrecuencia = findViewById(R.id.af_frecuencia)
        afTipo = findViewById(R.id.af_tipo)

        afSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                afFrecuencia.isEnabled = true
                afTipo.isEnabled = true
            }
        }

        afNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                afFrecuencia.isEnabled = false
                afTipo.isEnabled = false

                afFrecuencia.text.clear()
                afTipo.text.clear()

            }
        }

        //restriccion de actividad fisica
        sdSi= findViewById(R.id.sd_si)
        sdNo = findViewById(R.id.sd_no)

        //necesita apoyo para:

        naSi= findViewById(R.id.na_si)
        naNo= findViewById(R.id.na_no)
        naEspecifique= findViewById(R.id.na_especifique)


        naSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                naEspecifique.isEnabled= true
            }

        }

        naNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                naEspecifique.isEnabled= false
                naEspecifique.text.clear()
            }

        }

        //presencia de dolor en
        rdAbdomen= findViewById(R.id.rd_abdomen)
        rdPiernas = findViewById(R.id.rd_piernas)
        rdLumbar = findViewById(R.id.rd_lumbar)
        rdCutanea = findViewById(R.id.rd_cutanea)
        rdOtro= findViewById(R.id.rd_otro)
        rdElegirOtro = findViewById(R.id.rd_elegirOtro)


        rdOtro.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                rdElegirOtro.isEnabled = true
            } else {
                rdElegirOtro.isEnabled = false
                rdElegirOtro.text.clear()
            }
        }

        val otrosCheckBox = listOf(rdAbdomen, rdPiernas, rdLumbar, rdCutanea)

        setupExclusiveCheckBox(rdOtro,rdElegirOtro,otrosCheckBox)


        //6) MANTENIMIENTO DEL EQUILIBRIO ENTRE LA SOCIEDAD Y LA INTERACCION HUMANA

        puSi = findViewById(R.id.pu_si)
        puNo= findViewById(R.id.pu_no)
        puEspeficicar = findViewById(R.id.pu_especifique)

        puSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                puEspeficicar.isEnabled= true
            }
        }

        puNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                puEspeficicar.isEnabled= false
                puEspeficicar.text.clear()
            }

        }

        //utiliza apoyo para escuchar
        upSi= findViewById(R.id.up_si)
        upNo = findViewById(R.id.up_no)


        //presencia de:
        psMareo= findViewById(R.id.ps_mareo)
        psVertigo=findViewById(R.id.ps_vertigo)
        psAcuferos = findViewById(R.id.ps_acuferos)
        psLesiones = findViewById(R.id.ps_lesiones)
        psOtro = findViewById(R.id.ps_otro)

        //sistema ocular: problemas y/o alteracion visual
        pvSi= findViewById(R.id.pv_si)
        pvNo= findViewById(R.id.pv_no)
        pvEspecificar = findViewById(R.id.pv_especificar)

        pvSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                pvEspecificar.isEnabled= true
            }
        }

        pvNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                pvEspecificar.isEnabled= false
                pvEspecificar.text.clear()
            }
        }

        //utiliza
        utLentes = findViewById(R.id.ut_lentes)
        utArmazon = findViewById(R.id.ut_armazon)
        utContacto = findViewById(R.id.ut_contacto)
        utTiempoUso = findViewById(R.id.ut_tiempoUso)

        //observar presencia de
        opSeguridad = findViewById(R.id.op_seguridad)
        opTimidez = findViewById(R.id.op_timidez)
        opIntroversion = findViewById(R.id.op_introversion)
        opApatia = findViewById(R.id.op_apatia)
        opExtroversion = findViewById(R.id.op_extroversion)
        opAgresividad = findViewById(R.id.op_agresividad)
        opOtros = findViewById(R.id.op_otros)
        opElegirOtro= findViewById(R.id.op_ElejirOtro)


        opOtros.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                opElegirOtro.isEnabled = true
            } else {
                opElegirOtro.isEnabled = false
                opElegirOtro.text.clear()
            }
        }

        val presenciaCheckBox = listOf(opSeguridad, opTimidez, opIntroversion, opApatia, opExtroversion, opAgresividad)
        setupExclusiveCheckBox(opOtros, opElegirOtro, presenciaCheckBox)


        //la mayor parte del tiempo la pasa
        lpEnCasa = findViewById(R.id.lp_enCasa)
        lpConFamilia = findViewById(R.id.lp_conFamilia)
        lpTrabajo = findViewById(R.id.lp_trabajando)
        lpAmistades = findViewById(R.id.lp_amistades)

        //realiza actividades recreativas
        arSi = findViewById(R.id.ar_si)
        arNo = findViewById(R.id.ar_no)
        arEspeficicar = findViewById(R.id.ar_especificar)

        arSi.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                arEspeficicar.isEnabled= true
            }
        }

        arNo.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                arEspeficicar.isEnabled= false
                arEspeficicar.text.clear()
            }
        }

        //como considera sus relaciones
        saBuena = findViewById(R.id.sa_buena)
        saRegular = findViewById(R.id.sa_regular)
        saMala = findViewById(R.id.sa_mala)

        //cómo es la relacion con el personal de salud
        prBuena = findViewById(R.id.pr_buena)
        prRegular = findViewById(R.id.pr_regular)
        prMala = findViewById(R.id.pr_mala)

        //7) Prevención de peligros para la vida, el funcionamiento y bienestar humano
        //tipo y RH sanguineo
        tiEspecifiquerh = findViewById(R.id.ti_sanguineo)

        //presencia de heridas
        phSi= findViewById(R.id.ph_si)
        phNo= findViewById(R.id.ph_no)
        phTipo = findViewById(R.id.ph_tipo)
        phCaracteristica = findViewById(R.id.ph_caracteristicas)

        phSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                phTipo.isEnabled = true
                phCaracteristica.isEnabled = true
            }
        }

        phNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                phTipo.isEnabled = false
                phCaracteristica.isEnabled = false

                phTipo.text.clear()
                phCaracteristica.text.clear()
            }
        }

        //caracteristicas del acceso venoso
        veCaracteristicas = findViewById(R.id.ve_caracteristicas)

        //cateterismo vesical
        cvSi = findViewById(R.id.cv_si)
        cvNo = findViewById(R.id.cv_no)
        cvOtros = findViewById(R.id.cv_otros)
        cvSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cvOtros.isEnabled = true

            }
        }

        cvNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cvOtros.isEnabled = false
                cvOtros.text.clear()
            }
        }

        //Aceptacion de su aspecto fisico
        aiSi = findViewById(R.id.ai_si)
        aiNo = findViewById(R.id.ai_no)

        //pertecece a algun grupo social
        grSi= findViewById(R.id.gr_si)
        grNo = findViewById(R.id.gr_no)

        //aceptacion de los cambios a partir de su enfermedad
        efNo= findViewById(R.id.ef_no)
        efSi = findViewById(R.id.ef_si)

        //presencia de alteraciones emocionales
        erDepresion = findViewById(R.id.er_depresion)
        erAnsiedad = findViewById(R.id.er_ansiedad)
        erVerguenza = findViewById(R.id.er_verguenza)
        erTemor = findViewById(R.id.er_temor)
        erDesesperanza = findViewById(R.id.er_desesperanza)
        erOtro = findViewById(R.id.er_otro)
        erOtroEspecifique= findViewById(R.id.er_especifique)

        erOtro.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                erOtroEspecifique.isEnabled = true
            } else {
                erOtroEspecifique.isEnabled = false
                erOtroEspecifique.text.clear()
            }
        }

        val emocional= listOf(erDepresion, erAnsiedad, erVerguenza, erTemor,erDesesperanza)

        emocional.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    erOtro.isChecked = false  // desmarca "Otro"
                    erOtroEspecifique.isEnabled = false
                    erOtroEspecifique.text.clear()
                }
            }
        }


        //B DE DESARROLLO
        // Los que apoyan los procesos vitales
        eivs= findViewById(R.id.ivs_edad)

        //VSA
        vsaSi = findViewById(R.id.vsa_si)
        vsaNo = findViewById(R.id.vsa_no)

        //se protegio en su ultima relacion sexual
        ulSi = findViewById(R.id.ul_si)
        ulNo = findViewById(R.id.ul_no)

        //no de parejas sexuales
        noParejas = findViewById(R.id.nop_parejas)

        //conforme con sus preferencias sexuales
        rsSi = findViewById(R.id.rs_si)
        rsNo = findViewById(R.id.rs_no)

        //consideras las relaciones sexuales
        leNecesarias = findViewById(R.id.le_necesarias)
        lePlacenteras= findViewById(R.id.le_placenteras)
        leIncomodas = findViewById(R.id.le_incomodas)
        leForzosas = findViewById(R.id.le_forzosas)
        leAgradables = findViewById(R.id.le_agradables)
        leExcitantes = findViewById(R.id.le_excitantes)
        leDolorosas = findViewById(R.id.le_dolorosas)
        leOtro = findViewById(R.id.le_otro)
        leEspecifique = findViewById(R.id.le_especificar)

        leOtro.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                leEspecifique.isEnabled = true
            } else {
                leEspecifique.isEnabled = false
                leEspecifique.text.clear()
            }
        }
        val relaciones = listOf(leNecesarias,lePlacenteras,leIncomodas,leForzosas,leAgradables, leExcitantes,leDolorosas)
        setupExclusiveCheckBox(leOtro,leEspecifique,relaciones)


        //esquema de vacunacion
        svCompleto = findViewById(R.id.sv_completo)
        svIncompleto = findViewById(R.id.sv_incompleto)
        svEspecificar =findViewById(R.id.sv_especificar)

        //mujer edad de
        edMenarca = findViewById(R.id.ed_menarca)
        edTelarca = findViewById(R.id.ed_telarca)
        edPubarca = findViewById(R.id.ed_pubarca)
        edMenopausia = findViewById(R.id.ed_menopausia)

        //caracteristicas de la menstruacion
        mrRegular = findViewById(R.id.mr_regular)
        mrIrregular = findViewById(R.id.mr_irregular)
        mrDuracion = findViewById(R.id.mr_duracion)

        //metodos de planificacion
        poPlanificacion = findViewById(R.id.po_planificacion)

        //FUM
        mFum = findViewById(R.id.m_fum)

        mgGestas = findViewById(R.id.mg_gestas)
        mgPara= findViewById(R.id.mg_para)
        mgAbortos = findViewById(R.id.mg_abortos)
        mgCesareas= findViewById(R.id.mg_cesareas)
        mgNacidosVivos = findViewById(R.id.mg_nacidosVivos)

        //embarazo de alto riesgo
        ebSi= findViewById(R.id.eb_si)
        ebNo = findViewById(R.id.eb_no)

        //cirugia ap reproductor
        ebcSi= findViewById(R.id.ebc_si)
        ebcNo = findViewById(R.id.ebc_no)

        ebEspecifique = findViewById(R.id.eb_especifique)

        //ultimo papanicolau
        pfPapanicolau = findViewById(R.id.pf_papanicolado)
        pfResultado = findViewById(R.id.pf_resultado)

        //exploracion de mama
        exExploracionmama= findViewById(R.id.ex_mama)
        exResultado = findViewById(R.id.ex_resultado)

        //hombre examen de prostata
        exhProstata = findViewById(R.id.exh_prostata)
        exhEspecifique=findViewById(R.id.exh_especifique)
        exhResultado = findViewById(R.id.exh_resultado)

        //alteracion en aparato reprodutor
        atSi = findViewById(R.id.at_si)
        atNo = findViewById(R.id.at_no)
        atEspecificar = findViewById(R.id.at_especifique)

        //asistencia a capañas de salud
        cwSi = findViewById(R.id.cw_si)
        cwNo = findViewById(R.id.cw_no)

        //como responde ante situaciones de duelo o perdida
        fcSituaciones= findViewById(R.id.fc_situaciones)

        //cambio de residencia
        rwSi = findViewById(R.id.rw_si)
        rwNo= findViewById(R.id.rw_no)

        //casa
        cwcSi = findViewById(R.id.cwc_si)
        cwcNo = findViewById(R.id.cwc_no)

        //trabajo
        twcSi = findViewById(R.id.twc_si)
        twcNo = findViewById(R.id.twc_no)

        //familia
        fwcSi = findViewById(R.id.fwc_si)
        fwcNo = findViewById(R.id.fwc_no)

        //ingresos
        iwcSi = findViewById(R.id.iwc_si)
        iwcNo = findViewById(R.id.iwc_no)

        //se enferma con frecuencia
        enfSi = findViewById(R.id.enf_si)
        enfNo = findViewById(R.id.enf_no)

        //presenta problemas psicologicos
        pscSi = findViewById(R.id.psc_si)
        pscNo = findViewById(R.id.psc_no)

        //es autosufiente
        utsSi = findViewById(R.id.uts_si)
        utsNo = findViewById(R.id.uts_no)


        //5. peligro ambientales
        //tiene conctacto con:
        tccPesticidas = findViewById(R.id.tcc_pesticidas)
        tccBioxidoCarbono = findViewById(R.id.tcc_bioxidoCarbono)
        tccZonaInsalubre = findViewById(R.id.tcc_zonaInsalubre)

        //es adicto
        tccaSi = findViewById(R.id.tcca_si)
        tccaNo = findViewById(R.id.tcca_no)

        //convive con adictos
        tccSi = findViewById(R.id.tcc_si)
        tccNo = findViewById(R.id.tcc_no)
        tccEspecificar = findViewById(R.id.tcc_especificar)

        tccSi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tccEspecificar.isEnabled = true

            }
        }

        tccNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tccEspecificar.isEnabled = false
                tccEspecificar.text.clear()
            }
        }

        //adicciones en su entorno o usuarios
        tcceSi = findViewById(R.id.tcce_si)
        tcceNo = findViewById(R.id.tcce_no)

        //cruza las calles con precaucion
        crpSi = findViewById(R.id.crp_si)
        crpNo = findViewById(R.id.crp_no)

        //utiliza cinturon de seguridad
        utlSi = findViewById(R.id.utl_si)
        utlNo = findViewById(R.id.utl_no)

        //cierre las puertas con llave
        lavSi = findViewById(R.id.lav_si)
        lavNo = findViewById(R.id.lav_no)


        // cierra el tanque de gas
        cgaSi = findViewById(R.id.cga_si)
        cgaNo = findViewById(R.id.cga_no)


        //c) desviacion de la salud
        //requiere de cuidados especificso preventivos y regulares
        rpSi = findViewById(R.id.rp_si)
        rpNo = findViewById(R.id.rp_no)








        btnGuardar = findViewById(R.id.btn_guardar_seccion_a)

        btnGuardar.setOnClickListener {
            guardarDatos()
        }



        tvFechaIngreso.setOnClickListener {
            mostrarDatePicker(tvFechaIngreso)
        }



        mFum.setOnClickListener{
            mostrarDatePicker(mFum)
        }

        pfPapanicolau.setOnClickListener {
            mostrarDatePicker(pfPapanicolau)
        }



        pfResultado.setOnClickListener{
            mostrarDatePicker(pfResultado)
        }

        exhProstata.setOnClickListener {
            mostrarDatePicker(exhProstata)
        }

        exExploracionmama.setOnClickListener {
            mostrarDatePicker(exExploracionmama)
        }



    }



    private fun mostrarDatePicker(targetView: TextView) {
        val calendario = Calendar.getInstance()

        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, año, mes, dia ->
            val fechaSeleccionada = String.format("%02d/%02d/%04d", dia, mes + 1, año)
            targetView.text = fechaSeleccionada
        }, year, month, day)

        datePickerDialog.show()
    }





    private fun guardarDatos() {
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val idEstudiante = sharedPref.getString("ID_ESTUDIANTE", "") ?: ""

        val idEstudianteBase = etIdEstudiante.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val edad = etEdad.text.toString().trim()
        val sexoId = rgSexo.checkedRadioButtonId
        val religion = religion.text.toString().trim()
        val escolaridad = escolaridad.text.toString().trim()
        val ocupacion = ocupacion.text.toString().trim()
        val estadoCivil = estadoCivil.text.toString().trim()
        val fechaIngreso = tvFechaIngreso.text.toString().trim()


        //segunda parte
        val pisoId = rgPiso.checkedRadioButtonId
        val ventanasId = noVentanas.checkedRadioButtonId
        val luzId = luz.checkedRadioButtonId
        val animalesDomesticosnoID = noAnimalesDomesticos.checkedRadioButtonId
        val animalesVacunadosID = animalesVacunados.checkedRadioButtonId
        val relacionFamiliarID = relacionFamiliar.checkedRadioButtonId

        //checbox
        val paredes = mutableListOf<String>()
        if (cbBloque.isChecked) paredes.add("Bloque")
        if (cbLadrillo.isChecked) paredes.add("Ladrillo")
        if (cbAdobe.isChecked) paredes.add("Adobe")
        if (cbLamina.isChecked) paredes.add("Lámina")
        if (cbMadera.isChecked) paredes.add("Madera")
        if (cbParedVarias.isChecked) paredes.add("Más de una")
        val paredSeleccionada = paredes.joinToString(", ")

        //techo
        val techo = mutableListOf<String>()
        if (tcTeja.isChecked) techo.add("Teja")
        if (tcCarton.isChecked) techo.add("Carton")
        if (tcLamina.isChecked) techo.add("Lamina")
        if (tcConcreto.isChecked) techo.add("Concreto")
        val techoSeleccionada = techo.joinToString(", ")







        //  abasteciemiento de agua


        val abastecimientoAgua = mutableListOf<String>()
        if (cbLlave.isChecked) abastecimientoAgua.add("Llave")
        if (cbPozo.isChecked) abastecimientoAgua.add("Pozo")
        if (cbPipa.isChecked) abastecimientoAgua.add("Pipa")
        if (cbOtroAgua.isChecked) {
            val otroTexto = etAguaOtro.text.toString().trim()
            if (otroTexto.isNotEmpty()) {
                abastecimientoAgua.add("Otro: $otroTexto")
            }
        }
        val aguaSeleccionada = abastecimientoAgua.joinToString(", ")

        //purificacionAgua
        val purificacionAgua = mutableListOf<String>()
        if (meGarrafon.isChecked) purificacionAgua.add("Garrafon")
        if (meHervida.isChecked) purificacionAgua.add("Hervida")
        if (meClorada.isChecked) purificacionAgua.add("Clorada")
        if (meNinguno.isChecked) purificacionAgua.add("Ninguno")
        if (meMasDeUno.isChecked) purificacionAgua.add("Mas de Uno")
        val purificadaAguaSeleccionada = purificacionAgua.joinToString(", ")

        //Drenaje y Alcantarillado
        val drenaje = mutableListOf<String>()
        if (dcDrenaje.isChecked) drenaje.add("Drenaje")
        if (dcLetrina.isChecked) drenaje.add("Letrina")
        if (dcFosa.isChecked) drenaje.add("Fosa")
        if (dcSuelo.isChecked) drenaje.add("Suelo")
        val drenajeSeleccionada = drenaje.joinToString(", ")

        //basura
        val basura = mutableListOf<String>()
        if (trCamion.isChecked) basura.add("Camion Recolector")
        if (trQuema.isChecked) basura.add("Quema")
        if (trEnterrar.isChecked) basura.add("Enterrar")
        if (trMasDeUno.isChecked) basura.add("Mas de Uno")
        if (trNinguno.isChecked) basura.add("Ninguno")
        val basuraSeleccionada = basura.joinToString(", ")

        //fauna Nosiva
        val fauna = mutableListOf<String>()
        if (faRatones.isChecked) fauna.add("Ratones")
        if (faMoscas.isChecked) fauna.add("Moscas")
        if (faMosquitos.isChecked) fauna.add("Mosquitos")
        if (faArañas.isChecked) fauna.add("Arañas")
        if (faCucarachas.isChecked) fauna.add("Cucaracha")
        if (faPulgas.isChecked) fauna.add("Pulgas")
        if (faMasDeUno.isChecked) fauna.add("Mas de Uno")
        if (faNinguno.isChecked) fauna.add("Ninguno")
        val faunaSeleccionada = fauna.joinToString(", ")

        // Animales domesticos
        val domesticos = mutableListOf<String>()
        if (anPerros.isChecked) domesticos.add("Perros")
        if (anGatos.isChecked) domesticos.add("Gatos")
        if (anAves.isChecked) domesticos.add("Aves")
        if (anCerdos.isChecked) domesticos.add("Cerdos")
        if (anMasDeUno.isChecked) domesticos.add("Mas de uno")
        if (anOtro.isChecked) domesticos.add("Otro")
        if (anNinguno.isChecked) domesticos.add("Ninguno")
        val domesticosSeleccionada = domesticos.joinToString(", ")

        //SECCION B

        //ingreso economico
        val ingresoEconomico = mutableListOf<String>()
        if (ie1sm.isChecked) ingresoEconomico.add("menor 1 s.m.")
        if (ie12sm.isChecked) ingresoEconomico.add("1-2 s.m.")
        if (ie3sm.isChecked) ingresoEconomico.add("+ de 3 s.m.")
        if (ieninguno.isChecked) ingresoEconomico.add("Ninguno")
        val ingresoEconomicoSeleccionado = ingresoEconomico.joinToString(",")

        //dependencia economica
        val dependenciaEconomicaID = dependenciaEconomica.checkedRadioButtonId

        //estado nutricional
        val estadoNutricional = mutableListOf<String>()
        if (nuDesnutricion.isChecked) estadoNutricional.add("Desnutricion")
        if (nuNormal.isChecked) estadoNutricional.add("Normal")
        if (nuSobrepeso.isChecked) estadoNutricional.add("Sobrepeso")
        if (nuObesidad.isChecked) estadoNutricional.add("Obesidad")
        val estadoNutricionalSeleccionado = estadoNutricional.joinToString(",")

        //cabello

        val cabello = mutableListOf<String>()
        if (caquebradizo.isChecked) cabello.add("Quebradizo")
        if (caopaco.isChecked) cabello.add("Opaco")
        if (caescaso.isChecked) cabello.add("Escaso")
        if (camasdeuno.isChecked) cabello.add("Mas de uno")
        if (casaludable.isChecked) cabello.add("Saludable")
        val cabelloSeleccionado = cabello.joinToString(",")

        //mucosa
        val mucosa = mutableListOf<String>()
        if (muSangrado.isChecked) mucosa.add("Sangrado")
        if (muInfeccion.isChecked) mucosa.add("Infeccion")
        if (muPalidez.isChecked) mucosa.add("Palidez")
        if (muSaludable.isChecked) mucosa.add("Saludable")
        val mucosaSeleccionada = mucosa.joinToString(",")

        //piel
        val piel = mutableListOf<String>()
        if (piPielGallina.isChecked) piel.add("Piel de gallina")
        if (piSecaQuebrada.isChecked) piel.add("Seca y quegrada")
        if (piPuntosNegros.isChecked) piel.add("Puntos negros")
        if (piSecaMaltratada.isChecked) piel.add("Seca y maltratana")
        if (piEscamosa.isChecked) piel.add("Escamosa")
        if (piPalidez.isChecked) piel.add("Palidez")
        if (pimasdeuno.isChecked) piel.add("Mas de uno")
        if (piSaludable.isChecked) piel.add("Saludable")
        val pielSeleccionada = piel.joinToString(",")

        //labios
        val labios = mutableListOf<String>()
        if (laLesiones.isChecked) labios.add("Con lesiones")
        if (laSecos.isChecked) labios.add("Secos")
        if (laAgrietados.isChecked) labios.add("Agrietados")
        if (laMasdeuno.isChecked) labios.add("Mas de uno")
        if (laSaludables.isChecked) labios.add("Saludables")
        val labiosSeleccionada = labios.joinToString(",")

        //encias
        val encias = mutableListOf<String>()
        if (enHinchadas.isChecked) encias.add("Hinchadas")
        if (enSangran.isChecked) encias.add("Sangran fácilmente")
        if (enMasdeUno.isChecked) encias.add("Mas de uno")
        if (enSaludable.isChecked) encias.add("Saludable")
        val enciasSeleccionada = encias.joinToString(",")

        //nariz y orejas
        val narizorejas = mutableListOf<String>()
        if (orLesiones.isChecked) narizorejas.add("Con lesiones seborreicas")
        if (orHumedas.isChecked) narizorejas.add("Húmedas")
        if (orEnrojecidas.isChecked) narizorejas.add("Enrojecidas")
        if (orMasdeuno.isChecked) narizorejas.add("Mas de uno")
        if (orSaludables.isChecked) narizorejas.add("Saludables")
        val narizorejasSaludables = narizorejas.joinToString(",")

        //uñas
        val unas = mutableListOf<String>()
        if (asPalidas.isChecked) unas.add("Pálidas")
        if (asQuebradas.isChecked) unas.add("Quebradas")
        if (asEstriadas.isChecked) unas.add("Estriadas")
        if (asMasdeuno.isChecked) unas.add("Mas de uno")
        if (asSaludables.isChecked) unas.add("Saludables")
        val unasSeleccionada = unas.joinToString(",")

        //sistema oseo
        val sistemaOseo = mutableListOf<String>()
        if (soEncorvado.isChecked) sistemaOseo.add("Encorvado")
        if (soReduccion.isChecked) sistemaOseo.add("Reducción de talla")
        if (soDecalcificacion.isChecked) sistemaOseo.add("Descalcificación")
        if (soMasdeuno.isChecked) sistemaOseo.add("Mas de Uno")
        if (soSaludable.isChecked) sistemaOseo.add("Saludable")
        val sistemaOseoSeleccionado = sistemaOseo.joinToString(",")

        // en general
        val general = mutableListOf<String>()
        if (geDebilidad.isChecked) general.add("Debilidad")
        if (geFatiga.isChecked) general.add("Fatiga")
        if (geCansancio.isChecked) general.add("Cansancio")
        if (geCianosis.isChecked) general.add("Cianosis")
        if (geMasdeuno.isChecked) general.add("Mas de Uno")
        if (geSaludable.isChecked) general.add("Saludable")
        val generalSeleccionado = general.joinToString(",")

        //variacion seis meses
        val vpPesoId = vpPeso.checkedRadioButtonId
        val vpPesoRespuesta = if (vpPesoId != -1) {
            findViewById<RadioButton>(vpPesoId).text.toString()
        } else {
            "No seleccionado"
        }

        //kilos subido

        val kisubidoID = kisubido.checkedRadioButtonId

        //kilos perdido
        val kpPerdidoID = kpPerdido.checkedRadioButtonId

        //dentadura
        val dentadura = mutableListOf<String>()
        if (cdCompleta.isChecked) dentadura.add("Dientes completos")
        if (cdFrenos.isChecked) dentadura.add("Frenos")
        if (cdProtesis.isChecked) dentadura.add("Prótesis fija")
        if (cdMaloclusion.isChecked) dentadura.add("Mal oclusión")
        if (cdMasdeuno.isChecked) dentadura.add("Mas de uno")
        val dentaduraSeleccionado = dentadura.joinToString(",")


        ////////////////////////////////////////////////////////////////////////////////////////

        //problema de la cavidad oral
        val cavidadAgua = mutableListOf<String>()
        if (coNo.isChecked) cavidadAgua.add("No")

        if (coSi.isChecked) {
            val otroCavidad = etCavidadDetalle.text.toString().trim()
            if (otroCavidad.isNotEmpty()) {
                cavidadAgua.add("Si: $otroCavidad")
            }
        }
        val cavidadAguaSeleccionada = cavidadAgua.joinToString(", ")


        //tiene problemas dentales que le haga dificil comer
        val problemasDentales = mutableListOf<String>()
        if (deSi.isChecked) problemasDentales.add("Si")
        if (deNo.isChecked) problemasDentales.add("No")

        if (deSi.isChecked) {
            val otroDentales = etProblemasDentales.text.toString().trim()
            if (otroDentales.isNotEmpty()) {
                problemasDentales.add("Si: $otroDentales")

            } else {
                problemasDentales.add("No")

            }
        }
        val problemasDentalesSeleccionada = problemasDentales.joinToString(",")


        //digerir alimentos
        val digerirAlimentos = mutableListOf<String>()
        if (daNo.isChecked) digerirAlimentos.add("No")

        if (daSi.isChecked) {
            val otroDigerir = et_dijerirAlimentos.text.toString().trim()
            if (otroDigerir.isNotEmpty()) {
                digerirAlimentos.add("Si: $otroDigerir")

            }
        }
        val digerirAlimentosSeleccionada = digerirAlimentos.joinToString(",")

        //alimentos que no pueden comer

        val alimentosnocomen = mutableListOf<String>()
        if (alderivadosLeche.isChecked) alimentosnocomen.add("Derivados de la Leche")
        if (alcarnes.isChecked) alimentosnocomen.add("Carnes")
        if (alazucares.isChecked) alimentosnocomen.add("Azúcares")
        if (algrasas.isChecked) alimentosnocomen.add("Grasas")
        if (alsales.isChecked) alimentosnocomen.add("Sales")
        if (alcolesterol.isChecked) alimentosnocomen.add("Ricos en colesterol: huevo")
        if (almasdeuno.isChecked) alimentosnocomen.add("Mas de uno")
        if (alotros.isChecked) alimentosnocomen.add("Otros")
        if (alcometodo.isChecked) alimentosnocomen.add("Come de todo")
        val alimentosnocomenSeleccionada = alimentosnocomen.joinToString(",")


        //con que guisa sus alimentos
        val guisaAlimentos = mutableListOf<String>()
        if (agaceiteVegetal.isChecked) guisaAlimentos.add("Aceite vegetal")
        if (agaceiteAnimal.isChecked) guisaAlimentos.add("Aceite animal")
        if (agmanteca.isChecked) guisaAlimentos.add("Manteca")
        val guisaAlimentosSeleccionada = guisaAlimentos.joinToString(",")


        //come desayuno
        val comeDesayuno = mutableListOf<String>()
        if (cmlechecafe.isChecked) comeDesayuno.add("Leche/Café")
        if (cmpancereal.isChecked) comeDesayuno.add("Pan/Cereal")
        if (cmfrutatamales.isChecked) comeDesayuno.add("Fruta/Tamales")
        if (cmhuevoquesadillas.isChecked) comeDesayuno.add("Huevo/Quesadillas")
        if (cmrefrescojugo.isChecked) comeDesayuno.add("Refresco/Jugo de frutas")
        val comeDesayunoSelecccionada = comeDesayuno.joinToString(",")


        //come comida
        val comeComida = mutableListOf<String>()
        if (ccrefrecoagua.isChecked) comeComida.add("Refresco/Agua")
        if (ccverdurastacos.isChecked) comeComida.add("Verduras/Tacos")
        if (ccsopaarroz.isChecked) comeComida.add("Sopa maruchan/Arroz")
        if (cccarnesoya.isChecked) comeComida.add("Carne/Soya")
        if (ccfrijolesgorditas.isChecked) comeComida.add("Frijoles/Gorditas")
        val comeComidaSeleccionada = comeComida.joinToString(",")


        //que come en la cena
        val comeCena = mutableListOf<String>()
        if (qclechecafe.isChecked) comeCena.add("Leche/Cafe")
        if (qctacosfruta.isChecked) comeCena.add("Tacos/Fruta")
        if (qcyogurthtacos.isChecked) comeCena.add("Yogurth/Tacos")
        val comeCenaSeleccionada = comeCena.joinToString(",")

        //que come en la cena
        val cepilladoDientes = mutableListOf<String>()
        if (cddiario.isChecked) cepilladoDientes.add("Diario")
        if (cddosdias.isChecked) cepilladoDientes.add("Cada 2 días")
        if (cdtresdias.isChecked) cepilladoDientes.add("Cada 3 días")
        if (cdcuatrodias.isChecked) cepilladoDientes.add("+ de 4 días")
        if (cdesporadicamente.isChecked) cepilladoDientes.add("Esporádicamente")
        if (cdnunca.isChecked) cepilladoDientes.add("Nunca")
        val cepilladoDientesSeleccionada = cepilladoDientes.joinToString(",")


        //que come en la cena
        val seBaña = mutableListOf<String>()
        if (sbdiario.isChecked) seBaña.add("Diario")
        if (sbdosdias.isChecked) seBaña.add("Cada 2 días")
        if (sbtresdias.isChecked) seBaña.add("Cada 3 días")
        val seBañaSeleccionada = seBaña.joinToString(",")

        //cambio de ropa
        val cambioRopa = mutableListOf<String>()
        if (cpdiario.isChecked) cambioRopa.add("Diario")
        if (cpdosdias.isChecked) cambioRopa.add("Cada 2 días")
        if (cptresdias.isChecked) cambioRopa.add("Cada 3 días")
        val cambioRopaSeleccionada = cambioRopa.joinToString(",")

        //lavado de manos
        val lavadoManos = mutableListOf<String>()
        if (lmantescomer.isChecked) lavadoManos.add("Antes de comer")
        if (lmdespuesbano.isChecked) lavadoManos.add("Despues de ir al baño")
        if (lmantespreparar.isChecked) lavadoManos.add("Antes de preparar los alimentos ")
        if (lmmasdeunavez.isChecked) lavadoManos.add("+ de una vez")
        if (lmaveces.isChecked) lavadoManos.add("A veces")
        val lavadoManosSeleccionada = lavadoManos.joinToString(",")

        /////////////////////////////////////////////////////////////////////////////////////////////// seccion C

        // servicio de salud
        val servicioSalud = mutableListOf<String>()
        if (seimss.isChecked) servicioSalud.add("IMSS")
        if (seissste.isChecked) servicioSalud.add("ISSSTE")
        if (seisea.isChecked) servicioSalud.add("ISEA")
        if (separticular.isChecked) servicioSalud.add("Particular")
        if (seseguropopular.isChecked) servicioSalud.add("Seguro Popular")
        if (seotro.isChecked) servicioSalud.add("Otro")

        if (seotro.isChecked) {
            val otroSeguro = etServicioSocial.text.toString().trim()
            if (otroSeguro.isNotEmpty()) {
                servicioSalud.add("Otro: $otroSeguro")

            }
        }
        val servicioSaludSeleccionada = servicioSalud.joinToString(",")

        //cuando acude al servicio medico
        val acservicioID = acServicioMedico.checkedRadioButtonId

        //ES CAPAZ DE TOMAR DECISIONES RESPECTO A SU TRATAMIENTO
        val tomarDesiciones = mutableListOf<String>()
        if (drSi.isChecked) tomarDesiciones.add("Si")

        if (drNo.isChecked) {
            val otroTomarDesiciones = etDecisiones.text.toString().trim()
            if (otroTomarDesiciones.isNotEmpty()) {
                tomarDesiciones.add("No: $otroTomarDesiciones")

            }
        }
        val tomarDesicionesSeleccionada = tomarDesiciones.joinToString(",")

        //presencia de algunas enfermedades
        val presenciaEnfermedades = mutableListOf<String>()
        if (peSi.isChecked) presenciaEnfermedades.add("Si")
        if (peNo.isChecked) presenciaEnfermedades.add("No")

        if (peSi.isChecked) {
            val otropresenciaEnfermedades = etPresenciaEnfermedad.text.toString().trim()
            if (otropresenciaEnfermedades.isNotEmpty()) {
                presenciaEnfermedades.add("Si: $otropresenciaEnfermedades")

            }
        }
        val presenciaEnfermedadesSeleccionada = presenciaEnfermedades.joinToString(",")

        //tiene tratamiento
        val tieneTratamiento = mutableListOf<String>()
        if (trNo.isChecked) tieneTratamiento.add("No")

        if (trSi.isChecked) {
            val otrotieneTratamiento = et_tratamiento.text.toString().trim()
            if (otrotieneTratamiento.isNotEmpty()) {
                tieneTratamiento.add("Si: $otrotieneTratamiento")

            }
        }
        val tieneTratamientoSeleccionada = tieneTratamiento.joinToString(",")

        //lleva a cabo su tratamiento
        val llevaTratamiento = mutableListOf<String>()
        if (ctNo.isChecked) llevaTratamiento.add("No")
        if (ctSi.isChecked) llevaTratamiento.add("Si")
        val llevaTratamietoSeleccionada = llevaTratamiento.joinToString(",")


        //PARTE II MANTENIMENTO DE UN APORTE DE AIRE SUFICIENTE

        //frecuencia respiratoria
        val frrespiratoriasotro = frrespiratorias.text.toString().trim()


        //cianosis
        val tieneCianosis = mutableListOf<String>()
        if (ciaNo.isChecked) tieneCianosis.add("No")

        if (ciaSi.isChecked) {
            val otrotieneCianosis = ciaEspecifique.text.toString().trim()
            if (otrotieneCianosis.isNotEmpty()) {
                tieneCianosis.add("Si: $otrotieneCianosis")

            }
        }
        val tieneCianosis_seleccionada = tieneCianosis.joinToString(",")

        //llenado capilar
        val llenadoCapilar = mutableListOf<String>()
        if (lcSi.isChecked) llenadoCapilar.add("Si")
        if (lcNo.isChecked) llenadoCapilar.add("No")
        val llenadoCapilar_seleccionada = llenadoCapilar.joinToString(",")

        //fuma
        val fuma = mutableListOf<String>()
        if (fuSi.isChecked) fuma.add("Si")
        if (fuNo.isChecked) fuma.add("No")
        val fuma_seleccionada = fuma.joinToString(",")

        //no. cigarrillos al dia
        val cigarrosDia = csCigarrillos.text.toString().trim()

        //convive con fumadores
        val conviveFumadores = mutableListOf<String>()
        if (cnSi.isChecked) conviveFumadores.add("Si")
        if (cnNo.isChecked) conviveFumadores.add("No")
        val conviveFumadores_seleccionada = conviveFumadores.joinToString(",")


        //tos frecuente
        val tosFrecuente = mutableListOf<String>()
        if (tfNo.isChecked) tosFrecuente.add("No")
        if (tfSi.isChecked) tosFrecuente.add("Si")
        val tosFrecuenteSeleccionada = tosFrecuente.joinToString(",")


        //ruidos respiratorios anormales
        val respiracionAnormal = mutableListOf<String>()
        if (raNo.isChecked) respiracionAnormal.add("No")
        if (raSi.isChecked) respiracionAnormal.add("Si")
        val respiracionAnormalSeleccionada = respiracionAnormal.joinToString(",")


        //Factores desencadenantes de alteraciones respiratorias
        val factoresRespiracion = etFactoresAlteracion.text.toString().trim()


        //Oxigenoterapia
        val oxigenoterapia = mutableListOf<String>()
        if (oxpuntasnasales.isChecked) oxigenoterapia.add("Puntas nasales")
        if (oxmascarilla.isChecked) oxigenoterapia.add("Mascarilla")
        if (oxventiladormecanico.isChecked) oxigenoterapia.add("Ventilador Mecanico")
        if(oxotro.isChecked) oxigenoterapia.add("Otro")

        val oxigenoterapiaSeleccionada = oxigenoterapia.joinToString(",")


        //padece de enfermedades respiratorias/pulmonales
        val enf_respiraroriaPulmonal = mutableListOf<String>()
        if (enrNo.isChecked) enf_respiraroriaPulmonal.add("No")

        if (enrSi.isChecked) {
            val otroenf_respiratoriaPulmonal = et_enfermedadesRespitatorias.text.toString().trim()
            if (otroenf_respiratoriaPulmonal.isNotEmpty()) {
                enf_respiraroriaPulmonal.add("Si: $otroenf_respiratoriaPulmonal")

            }
        }
        val enf_respiratoriaPulmonalSeleccionada = enf_respiraroriaPulmonal.joinToString(",")


        //cardiovasculares
        val ca_frecuenciaCardiaca = crFrecuenciaCardiaca.text.toString().trim()
        val ca_TA = crTA.text.toString().trim()
        val ca_PVC = crPVC.text.toString().trim()



        //valoracion de extremidades inferiores

        val extremidadesInferiores = mutableListOf<String>()
        if (vxSi.isChecked) extremidadesInferiores.add("Si")
        if (vxNo.isChecked) extremidadesInferiores.add("No")
        val extremidadesInferiores_seleccionada = extremidadesInferiores.joinToString(",")

        //clasificacion
        val extremidades_clasificacion = vxClasificacion.text.toString().trim()

        //pulso
        val extremidades_pulso = vxPulso.text.toString().trim()

        //temp
        val extremidades_temp = vxTemp.text.toString().trim()

        //coloracion
        val extremidades_coloracion = vxColoracion.text.toString().trim()

        //llenado capilar
        val extremidades_llenadoCapilar = vxLlenadoCapilar.text.toString().trim()

        //edema
        val edema = mutableListOf<String>()
        if (vxesi.isChecked) edema.add("Si")
        if (vxeno.isChecked) edema.add("No")
        val edema_seleccionada = edema.joinToString(",")





      //  Mantenimiento de un aporte de agua suficiente

        //consumo habitual de agua aproximadamente por dia
        val consumoHabitualAgua = chConsumoAgua.text.toString().trim()


        //origen del consumo de agua
        val consumoAgua = mutableListOf<String>()
        if (ocGarrafon.isChecked) consumoAgua.add("Garrafon")
        if (ocHervida.isChecked) consumoAgua.add("Hervida")
        if (ocClorada.isChecked) consumoAgua.add("Clorada")
        if (ocPotable.isChecked) consumoAgua.add("Potable")
        val consumoAguaSeleccionada = consumoAgua.joinToString(",")
        val ocotroTipoLiquido = ocOtroLiquido.text.toString().trim()
        val ocCantidadhoras = ocCantidad.text.toString().trim()

        //presencia datos deshidratacion
        val deshidratacion = mutableListOf<String>()
        if (pdMucosaOral.isChecked) deshidratacion.add("Mucosa oral")
        if (pdCabello.isChecked) deshidratacion.add("Cabello")
        if (pdTurgencua.isChecked) deshidratacion.add("Turgencia")
        val deshidratacionSeleccionada = deshidratacion.joinToString(",")

        //consumo de alcohol
        val consumoAlcohol = mutableListOf<String>()

        if (casi.isChecked) consumoAlcohol.add("Sí")
        if (cano.isChecked) consumoAlcohol.add("No")

        if (casi.isChecked) {
            val especificar = pdEspecificar.text.toString().trim()
            val frecuencia = pdFrecuencia.text.toString().trim()
            val cantidad = pdCantidad.text.toString().trim()

            if (especificar.isNotEmpty()) consumoAlcohol.add("Especifique: $especificar")
            if (frecuencia.isNotEmpty()) consumoAlcohol.add("Frecuencia: $frecuencia")
            if (cantidad.isNotEmpty()) consumoAlcohol.add("Cantidad: $cantidad")
        }

        val consumoAlcoholSeleccionado = consumoAlcohol.joinToString(",")

        //tipo de producto de alcohol
        val tipoProducto = mutableListOf<String>()

        // Si alguno está marcado, lo agregamos
        if (tpCerveza.isChecked) tipoProducto.add("Cerveza")
        if (tpTequila.isChecked) tipoProducto.add("Tequila")
        if (tpVinosMesa.isChecked) tipoProducto.add("Vinos de mesa")
        if (tpPulque.isChecked) tipoProducto.add("Pulque")

        // Si ninguno está marcado y el campo "Otro" está habilitado
        if (tpOtroCheckbox.isChecked) {
            val otroTexto = tpOtro.text.toString().trim()
            if (otroTexto.isNotEmpty()) {
                tipoProducto.add("Otro: $otroTexto")
            } else {
                tipoProducto.add("Otro")
            }
        }
        // Convertimos en cadena separada por comas
        val tipoProductoSeleccionado = tipoProducto.joinToString(",")


        ///////////// 3) MANTENIMIENTO DE UN APORTE DE ALIMENTO SUFICENTE

        //somatometria
        val sopeso = soPeso.text.toString().trim()
        val sotalla = soTalla.text.toString().trim()
        val soimc = soImc.text.toString().trim()
        val socintura = soCintura.text.toString().trim()

        val soperimetroabdominal = soPerimetroAbdominal.text.toString().trim()
        val sonumerocomidadia = soNumeroComidaDia.text.toString().trim()
        val comeFamiliaID = soComeFamilia.checkedRadioButtonId


        //Ingesta de suplementos alimenticios ESTE USO RADIOBUTTON Y TAMBIEN SE GUARDO LOS DATOS
        val ingestaSuplementos = mutableListOf<String>()

        if (isSuplementosSi.isChecked) ingestaSuplementos.add("Sí")
        if (isSuplementosNo.isChecked) ingestaSuplementos.add("No")

        if (isSuplementosSi.isChecked) {
            val elegirCuales = isCuales.text.toString().trim()

            if (elegirCuales.isNotEmpty()) ingestaSuplementos.add(" $elegirCuales")


        }
        val ingestaSuplementosSeleccionado = ingestaSuplementos.joinToString(",")

        //presencia de

        val presenciaDe = mutableListOf<String>()
        if (pdnauseas.isChecked) presenciaDe.add("Náuseas")
        if (pdhematemesis.isChecked) presenciaDe.add("Hematemesis")
        if (pdpolifagia.isChecked) presenciaDe.add("Polifagia")
        if (pdpirosis.isChecked) presenciaDe.add("Pirosis")

        if (pdotro.isChecked) presenciaDe.add("Otro")


        if (pdotro.isChecked) {
            val otropresenciaDe = editpdotro.text.toString().trim()
            if (otropresenciaDe.isNotEmpty()) {
                presenciaDe.add("Si: $otropresenciaDe")
            } else {
                cavidadAgua.add("No")
            }
        }
        val presenciaDeSeleccionada = presenciaDe.joinToString(", ")


        //eliminacion vesical
        //incontinencia
        val noVecesDia = evNoveces.text.toString().trim()
        val eliminacionVesical_Incontinencia = mutableListOf<String>()
        if (eviSi.isChecked) eliminacionVesical_Incontinencia.add("Si")
        if (eviNo.isChecked) eliminacionVesical_Incontinencia.add("No")
        val eliminacionVesicalIncontinencia_Seleccionada =
            eliminacionVesical_Incontinencia.joinToString(",")

        //retencion
        val eliminarRetencion = mutableListOf<String>()
        if (evrSi.isChecked) eliminarRetencion.add("Si")
        if (evrNo.isChecked) eliminarRetencion.add("No")
        val eliminarRetencion_seleccionada = eliminarRetencion.joinToString(",")

        //dolor
        val eliminarDolor = mutableListOf<String>()
        if (evdSi.isChecked) eliminarDolor.add("Si")
        if (evdNo.isChecked) eliminarDolor.add("No")
        val eliminarDolor_seleccionada = eliminarDolor.joinToString(",")

        //caracteristicas de la orina
        val caracteristicasOrina = coCantidad.text.toString().trim()
        val cantidadOrina = coCantidad.text.toString().trim()

        //uso de auxiliares para orina
        val auxiliaresOrina = mutableListOf<String>()
        if (uaSi.isChecked) auxiliaresOrina.add("Sí")
        if (uaNo.isChecked) auxiliaresOrina.add("No")

        if (uaSi.isChecked) {
            val auxiliarOrina = uaEspecificar.text.toString().trim()

            if (auxiliarOrina.isNotEmpty()) auxiliaresOrina.add(" $auxiliarOrina")


        }
        val auxiliarOrina_seleccionado = auxiliaresOrina.joinToString(",")

        //eliminacion intestinal
        val frecuenciaIntestinal = eiFrecuencia.text.toString().trim()

        val eliminacionIntestinal = mutableListOf<String>()
        if (eiDolorAbdominal.isChecked) eliminacionIntestinal.add("Dolor abdominal")
        if (eiFlatulencias.isChecked) eliminacionIntestinal.add("Flatulencias")
        if (eiEstrenimiento.isChecked) eliminacionIntestinal.add("Estreñimiento")
        if (eiPujo.isChecked) eliminacionIntestinal.add("Pujo")
        if (eiDiarrea.isChecked) eliminacionIntestinal.add("Diarrea")
        if (eiMelena.isChecked) eliminacionIntestinal.add("Melena")
        if (eiTenesmo.isChecked) eliminacionIntestinal.add("Tenesmo")
        if (eiDolorEvacual.isChecked) eliminacionIntestinal.add("Dolor al evacuar")
        if (eiHemorroides.isChecked) eliminacionIntestinal.add("Hemorroides")
        val eliminacionIntestinal_seleccionada = eliminacionIntestinal.joinToString(",")

        //caracteristica de la evacuacion
        val caracteristicaEvacuacion = ceEspecificar.text.toString().trim()

        //uso de auxiliar para evacuar
        val auxiliarEvacuar = mutableListOf<String>()
        if (aeSi.isChecked) auxiliarEvacuar.add("Sí")
        if (aeNo.isChecked) auxiliarEvacuar.add("No")

        if (aeSi.isChecked) {
            val evacuar = ae_Especificar.text.toString().trim()

            if (evacuar.isNotEmpty()) auxiliarEvacuar.add(" $evacuar")


        }
        val auxiliarEvacuar_seleccionado = auxiliarEvacuar.joinToString(",")


        //secreción transvaginal
        val secrecionTransvaginal = mutableListOf<String>()
        if (stSi.isChecked) secrecionTransvaginal.add("Si")
        if (stNo.isChecked) secrecionTransvaginal.add("No")

        if (stSi.isChecked) {
            val secrecion = stCaracteristicas.text.toString().trim()

            if (secrecion.isNotEmpty()) secrecionTransvaginal.add(" $secrecion")


        }
        val secrecionTransvaginal_seleccionado = secrecionTransvaginal.joinToString(",")

        // 5) MANTENIMIENTO DEL EQUILIBRIO ENTRE LA ACTIVIDAD Y EL REPOSO

        //reposo
        val reposo = reNoHorasNocturno.text.toString().trim()

        //realiza siestas diurnas
        val siestasDiurnas = mutableListOf<String>()
        if (reSi.isChecked) siestasDiurnas.add("Si")
        if (reNo.isChecked) siestasDiurnas.add("No")

        if (reSi.isChecked) {
            val siestas = reNoHoras.text.toString().trim()
            if (siestas.isNotEmpty()) siestasDiurnas.add(" $siestas")
        }
        val siestasDiurnas_seleccionada = siestasDiurnas.joinToString(",")

        //¿Tiene dificultad para conciliar el sueño?
        val dificultadSueno = mutableListOf<String>()
        if (dsSi.isChecked) dificultadSueno.add("Si")
        if (dsNo.isChecked) dificultadSueno.add("No")
        val dificultadSueno_seleccionada = dificultadSueno.joinToString(",")

        // ¿Como se encuentra al despertar?
        val encuentraDespertar = mutableListOf<String>()
        if (eaCansado.isChecked) encuentraDespertar.add("Cansado")
        if (eaDescansado.isChecked) encuentraDespertar.add("Descansado")
        if (eaOtro.isChecked) encuentraDespertar.add("Otro")
        val encuentraDespertar_seleccionado = encuentraDespertar.joinToString(",")

        //presencia de:
        val presencia_de = mutableListOf<String>()
        if (prBostezo.isChecked) presencia_de.add("Bostezo")
        if (prPesadilla.isChecked) presencia_de.add("Pesadillas")
        if (prOjeras.isChecked) presencia_de.add("Ojeras")
        if (prRonquidos.isChecked) presencia_de.add("Ronquidos")
        if (prNinguno.isChecked) presencia_de.add("Ninguno")
        val presencia_seleccionado = presencia_de.joinToString(",")

        //uso de apoyos para dormir
        val apoyoDormir = mutableListOf<String>()
        if (adSi.isChecked) apoyoDormir.add("Si")
        if (adNo.isChecked) apoyoDormir.add("No")

        if (adSi.isChecked) {
            val usoApoyo = adEspecificar.text.toString().trim()
            if (usoApoyo.isNotEmpty()) apoyoDormir.add(" $usoApoyo")
        }
        val apoyoDormir_seleccionado = apoyoDormir.joinToString(",")

        //despierta por la noche
        val despiertaNoche = mutableListOf<String>()
        if (dnSi.isChecked) despiertaNoche.add("Si")
        if (dnNo.isChecked) despiertaNoche.add("No")

        if (dnSi.isChecked) {
            val despierta = dnMotivo.text.toString().trim()
            if (despierta.isNotEmpty()) despiertaNoche.add(" $despierta")
        }
        val despiertaNoche_seleccionado = despiertaNoche.joinToString(",")

        //actividad: realiza actividad fisica
        val actividadFisica = mutableListOf<String>()
        if (afSi.isChecked) actividadFisica.add("Sí")
        if (afNo.isChecked) actividadFisica.add("No")

        if (afSi.isChecked) {
            val frecuencia = afFrecuencia.text.toString().trim()
            val tipo = afTipo.text.toString().trim()

            if (frecuencia.isNotEmpty()) actividadFisica.add("Frecuencia: $frecuencia")
            if (tipo.isNotEmpty()) actividadFisica.add("Tipo: $frecuencia")
        }
        val actividadFisica_seleccionado = actividadFisica.joinToString(",")

        //restriccion de actividad fisica
        val restriccionActividad = mutableListOf<String>()
        if (afSi.isChecked) restriccionActividad.add("Si")
        if (afNo.isChecked) restriccionActividad.add("No")
        val restriccionFisica_seleccionada = restriccionActividad.joinToString(",")

        //necesita apoyo para:
        val apoyo = mutableListOf<String>()
        if (naSi.isChecked) apoyo.add("Si")
        if (naNo.isChecked) apoyo.add("No")

        if (naSi.isChecked) {
            val naespecifique = naEspecifique.text.toString().trim()
            if (naespecifique.isNotEmpty()) apoyo.add(" $naespecifique")
        }
        val apoyo_seleccionado = apoyo.joinToString(",")

        //presencia de dolor en:
        val presenciaDolor = mutableListOf<String>()
        if (rdAbdomen.isChecked) presenciaDolor.add("Abdomen")
        if (rdPiernas.isChecked) presenciaDolor.add("Piernas")
        if (rdLumbar.isChecked) presenciaDolor.add("Lumbar")
        if (rdCutanea.isChecked) presenciaDolor.add("Cutánea")
        if (rdOtro.isChecked) presenciaDolor.add("Otro")
        if (rdOtro.isChecked) {
            val rdOtro = rdElegirOtro.text.toString().trim()
            if (rdOtro.isNotEmpty()) presenciaDolor.add(" $rdOtro")
        }
        val presenciaDolor_seleccionado = presenciaDolor.joinToString(",")

        //6) MANTENIMIENTO DEL EQUILIBRIO ENTRE LA SOCIEDAD Y LA INTERACCION HUMANA

        //problemas auditivos
        val problemasAuditivo = mutableListOf<String>()
        if (puSi.isChecked) problemasAuditivo.add("Si")
        if (puNo.isChecked) problemasAuditivo.add("No")
        if (puSi.isChecked) {
            val puEspecificar = puEspeficicar.text.toString().trim()
            if (puEspecificar.isNotEmpty()) problemasAuditivo.add(" $puEspecificar")
        }
        val problemasAuditivos_seleccionado = problemasAuditivo.joinToString(",")

        //utiliza apoyo para escuchar
        val apoyoEscuchar = mutableListOf<String>()
        if (upSi.isChecked) apoyoEscuchar.add("Si")
        if (upNo.isChecked) apoyoEscuchar.add("No")
        val apoyoEscuchar_seleccionada = apoyoEscuchar.joinToString(",")

        //presencia de
        val presenciaSistema = mutableListOf<String>()
        if (psMareo.isChecked) presenciaSistema.add("Mareo")
        if (psVertigo.isChecked) presenciaSistema.add("Vértigo")
        if (psAcuferos.isChecked) presenciaSistema.add("Acúferos")
        if (psLesiones.isChecked) presenciaSistema.add("Lesiones")
        if (psOtro.isChecked) presenciaSistema.add("Otro")
        val presenciaSistema_seleccionada = presenciaSistema.joinToString(",")


        //sistema ocular problemas y/o alteracion visual
        val alteracionVisual = mutableListOf<String>()
        if (pvSi.isChecked) alteracionVisual.add("Si")
        if (pvNo.isChecked) alteracionVisual.add("No")

        if (pvSi.isChecked) {
            val pvEspecificar = pvEspecificar.text.toString().trim()
            if (pvEspecificar.isNotEmpty()) alteracionVisual.add(" $pvEspecificar")
        }
        val alteracionVisual_seleccionado = alteracionVisual.joinToString(",")

        //utiliza lentes
        val utilizaLentes = mutableListOf<String>()
        if (utLentes.isChecked) utilizaLentes.add("Lentes")
        if (utArmazon.isChecked) utilizaLentes.add("Armazón")
        if (utContacto.isChecked) utilizaLentes.add("Contacto")
        val utilizaLentes_seleccionada = utilizaLentes.joinToString(",")
        val tiempoUso_seleccionada = utTiempoUso.text.toString().trim()

        //observar presencia de
        val observarPresencia = mutableListOf<String>()
        if (opSeguridad.isChecked) observarPresencia.add("Seguridad")
        if (opTimidez.isChecked) observarPresencia.add("Timidez")
        if (opIntroversion.isChecked) observarPresencia.add("Introversión")
        if (opApatia.isChecked) observarPresencia.add("Apatía")
        if (opExtroversion.isChecked) observarPresencia.add("Extroversión")
        if (opAgresividad.isChecked) observarPresencia.add("Agresividad")
        if (opOtros.isChecked) observarPresencia.add("Otro")

        if (opOtros.isChecked) {
            val opOtros = opElegirOtro.text.toString().trim()
            if (opOtros.isNotEmpty()) observarPresencia.add(" $opOtros")


        }
        val observarPresencia_seleccionado = observarPresencia.joinToString(",")



        //la mayor parte del tiempo la pasa
        val mayorParte = mutableListOf<String>()
        if (lpEnCasa.isChecked) mayorParte.add("En casa sola")
        if (lpConFamilia.isChecked) mayorParte.add("Con la familia")
        if (lpTrabajo.isChecked) mayorParte.add("Trabajo")
        if (lpAmistades.isChecked) mayorParte.add("Amistades")
        val mayorParte_seleccionado = mayorParte.joinToString(",")

        //realiza actividades recreativas
        val actividadesRecreativas = mutableListOf<String>()
        if (arSi.isChecked) actividadesRecreativas.add("Si")
        if (arNo.isChecked) actividadesRecreativas.add("No")


        if (arSi.isChecked) {
            val arEspecificar = arEspeficicar.text.toString().trim()
            if (arEspecificar.isNotEmpty()) actividadesRecreativas.add(" $arEspecificar")
        }
        val actividadRecreativa_seleccionado = actividadesRecreativas.joinToString(",")

        //como considera sus relaciones
        val consideraRelaciones = mutableListOf<String>()
        if (saBuena.isChecked) consideraRelaciones.add("Buena")
        if (saRegular.isChecked) consideraRelaciones.add("Regular")
        if (saMala.isChecked) consideraRelaciones.add("Mala")
        val relaciones_seleccionado = consideraRelaciones.joinToString(",")

        //como es la relacion con el personal de salud
        val personalSalud = mutableListOf<String>()
        if (prBuena.isChecked) personalSalud.add("Buena")
        if (prRegular.isChecked) personalSalud.add("Regular")
        if (prMala.isChecked) personalSalud.add("Mala")
        val personalsalud_seleccionado = personalSalud.joinToString(",")

        //7) Prevención de peligros para la vida, el funcionamiento y bienestar humano
        //tipo y rh sanguineo
        val tipoSanguineo = tiEspecifiquerh.text.toString().trim()

        //presencia de heridas
        val presenciaHeridas = mutableListOf<String>()
        if (phSi.isChecked) presenciaHeridas.add("Sí")
        if (phNo.isChecked) presenciaHeridas.add("No")

        if (phSi.isChecked) {
            val phtipo = phTipo.text.toString().trim()
            val phcaracteristica = phCaracteristica.text.toString().trim()

            if (phtipo.isNotEmpty()) presenciaHeridas.add("Tipo: $phtipo")
            if (phcaracteristica.isNotEmpty()) presenciaHeridas.add("Características: $phcaracteristica")
        }
        val presenciaHeridas_seleccionado = presenciaHeridas.joinToString(",")

        //caracteristicas del acceso venoso
        val accesoVenoso = veCaracteristicas.text.toString().trim()

        //cateterismo vesical
        val vesical = mutableListOf<String>()
        if (cvSi.isChecked) vesical.add("Si")
        if (cvNo.isChecked) vesical.add("No")

        if (cvSi.isChecked) {
            val cvVesical = cvOtros.text.toString().trim()

            if (cvVesical.isNotEmpty()) vesical.add("Otros: $cvVesical")
        }
        val vesical_seleccionado = vesical.joinToString(",")

        //Aceptacion de su aspecto fisico
        val aceptacionFisico = mutableListOf<String>()
        if (aiSi.isChecked) aceptacionFisico.add("Si")
        if (aiNo.isChecked) aceptacionFisico.add("No")
        val aceptacionFisico_seleccionada = aceptacionFisico.joinToString(",")

        //pertenece a algun grupo social
        val grupoSocial = mutableListOf<String>()
        if (grSi.isChecked) grupoSocial.add("Si")
        if (grNo.isChecked) grupoSocial.add("No")
        val grupoSocial_seleccionada = grupoSocial.joinToString(",")

        //aceptacion de los cambios a partir de su enfermedad
        val cambiosEnfermedad = mutableListOf<String>()
        if (efSi.isChecked) cambiosEnfermedad.add("Si")
        if (efNo.isChecked) cambiosEnfermedad.add("No")
        val cambiosEnfermedad_seleccionada = cambiosEnfermedad.joinToString(",")

        //presencia de alteraciones emocionales
        val alteracionEmocionales = mutableListOf<String>()
        if (erDepresion.isChecked) alteracionEmocionales.add("Depresión")
        if (erAnsiedad.isChecked) alteracionEmocionales.add("Ansiedad")
        if (erVerguenza.isChecked) alteracionEmocionales.add("Vergüenza")
        if (erTemor.isChecked) alteracionEmocionales.add("Temor")
        if (erDesesperanza.isChecked) alteracionEmocionales.add("Desesperanza")
        if (erOtro.isChecked) alteracionEmocionales.add("Otro")


        if (erOtro.isChecked) {
            val erotros = erOtroEspecifique.text.toString().trim()
            if (erotros.isNotEmpty()) alteracionEmocionales.add(" $erotros")


        }
        val alteracionEmocionales_seleccionado = alteracionEmocionales.joinToString(",")

        //los que apoyan procesos vitales
        val edadivs = eivs.text.toString().trim()

        //VSA
        val vsa = mutableListOf<String>()
        if (vsaSi.isChecked) vsa.add("Si")
        if (vsaNo.isChecked) vsa.add("No")
        val vsa_seleccionada = vsa.joinToString(",")

        //se protegio en su ultima relacion sexual
        val seprotegio = mutableListOf<String>()
        if (ulSi.isChecked) seprotegio.add("Si")
        if (ulNo.isChecked) seprotegio.add("No")
        val seProtegio_seleccionada = seprotegio.joinToString(",")

        //no de parejas sexuales
        val noParejas = noParejas.text.toString().trim()

        //conforme con sus preferencias sexuales
        val conformePreferencias = mutableListOf<String>()
        if (rsSi.isChecked) conformePreferencias.add("Si")
        if (rsNo.isChecked) conformePreferencias.add("No")
        val conformePreferencias_seleccionada = conformePreferencias.joinToString(",")


        //consideras las relaciones sexuales
        val relaciones = mutableListOf<String>()
        if (leNecesarias.isChecked) relaciones.add("Necesarias")
        if (lePlacenteras.isChecked) relaciones.add("Placenteras")
        if (leIncomodas.isChecked) relaciones.add("Incómodas")
        if (leForzosas.isChecked) relaciones.add("Forzosas")
        if (leAgradables.isChecked) relaciones.add("Agradables")
        if (leExcitantes.isChecked) relaciones.add("Excitantes")
        if (leDolorosas.isChecked) relaciones.add("Dolorosas")
        if (leOtro.isChecked) {
            val lerotro = leEspecifique.text.toString().trim()
            if (lerotro.isNotEmpty()) relaciones.add(" $lerotro")


        }
        val relacionesSexuales_seleccionada = relaciones.joinToString(",")

        //esquema de vacunacion
        val esquemaVacunacion = mutableListOf<String>()
        if (svCompleto.isChecked) esquemaVacunacion.add("Completo")
        if (svIncompleto.isChecked) esquemaVacunacion.add("Incompleto")
        val esquemaVacunacion_seleccionado = esquemaVacunacion.joinToString(",")
        val vacunacionEspecificar = svEspecificar.text.toString().trim()

        //mujer edad de
        val edmenarca = edMenarca.text.toString().trim()
        val edtelarca = edTelarca.text.toString().trim()
        val edpubarca = edPubarca.text.toString().trim()
        val edmenopausia = edMenopausia.text.toString().trim()

        //caracteristicas de la menstruacion
        val menstruacion = mutableListOf<String>()
        if (mrRegular.isChecked) menstruacion.add("Regular")
        if (mrIrregular.isChecked) menstruacion.add("Irregular")
        val menstruacion_seleccionada = menstruacion.joinToString(",")
        val menstruacionDuracion = mrDuracion.text.toString().trim()


        //metodo de planificacion
        val pometodo = poPlanificacion.text.toString().trim()

        val gestasmg = mgGestas.text.toString().trim()
        val paramg = mgPara.text.toString().trim()
        val abortomg = mgAbortos.text.toString().trim()
        val cesareamg = mgCesareas.text.toString().trim()
        val nacidosVivosmg = mgNacidosVivos.text.toString().trim()


        //embarazo de alto riesgo
        val altoRiesgo = mutableListOf<String>()
        if (ebSi.isChecked) altoRiesgo.add("Si")
        if (ebNo.isChecked) altoRiesgo.add("No")
        val altoRiesgo_seleccionada = altoRiesgo.joinToString(",")

        //cirugias ap. reproductor
        val cirugiaap = mutableListOf<String>()
        if (ebcSi.isChecked) cirugiaap.add("Si")
        if (ebcNo.isChecked) cirugiaap.add("No")
        val cirugiaap_seleccionada = cirugiaap.joinToString(",")
        val ebespecifique = ebEspecifique.text.toString().trim()

        //FUM
        val noFum = mFum.text.toString().trim()

        //ultimo papanicolau
        val pfpapanicolau = pfPapanicolau.text.toString().trim()


        //ultimo papanicolau
        val pfresultado = pfResultado.text.toString().trim()

        //exploracion de mama
        val exmama = exExploracionmama.text.toString().trim()
        val exresultado = exResultado.text.toString().trim()

        //hombre examen de prostata
        val exhexamen = exhProstata.text.toString().trim()
        val exhespecifique = exhEspecifique.text.toString().trim()
        val exhresultado = exhResultado.text.toString().trim()

        //alteracion en aparato reproductor
        val alteracionReproductor = mutableListOf<String>()
        if (atSi.isChecked) alteracionReproductor.add("Si")
        if (atSi.isChecked) alteracionReproductor.add("No")
        val alteracionReproductor_seleccionada = alteracionReproductor.joinToString(",")
        val alteracionReproductor_especifique = atEspecificar.text.toString().trim()

        //asistencia a capañas de salud
        val asistenciaCampana = mutableListOf<String>()
        if(cwSi.isChecked) asistenciaCampana.add("Si")
        if(cwNo.isChecked) asistenciaCampana.add("No")
        val asistenciaCampana_seleccionada = asistenciaCampana.joinToString(",")

        //como responde ante situaciones de duelo o pérdida
        val situacionesPerdida = fcSituaciones.text.toString().trim()

        //cambio de residencia
        val cambioResidencia= mutableListOf<String>()
        if(rwSi.isChecked) cambioResidencia.add("Si")
        if(rwNo.isChecked) cambioResidencia.add("No")
        val cambioResidencia_seleccionada = cambioResidencia.joinToString(",")

        //casa
        val casa= mutableListOf<String>()
        if(cwcSi.isChecked) casa.add("Si")
        if(cwcNo.isChecked) casa.add("No")
        val casa_seleccionada = casa.joinToString(",")

        //trabajo
        val trabajo= mutableListOf<String>()
        if(twcSi.isChecked) trabajo.add("Si")
        if(twcNo.isChecked) trabajo.add("No")
        val trabajo_seleccionada = trabajo.joinToString(",")

        //familia
        val familia= mutableListOf<String>()
        if(fwcSi.isChecked) familia.add("Si")
        if(fwcNo.isChecked) familia.add("No")
        val familia_seleccionada = familia.joinToString(",")

        //ingresos
        val ingresos= mutableListOf<String>()
        if(iwcSi.isChecked) ingresos.add("Si")
        if(iwcNo.isChecked) ingresos.add("No")
        val ingresos_seleccionada = ingresos.joinToString(",")


        //4. Mala salud, condiciones de vida o incapacidad, enfermedad terminal
        //se enferma con frecuencia
        val enfermaFrecuencia= mutableListOf<String>()
        if(enfSi.isChecked) enfermaFrecuencia.add("Si")
        if(enfNo.isChecked) enfermaFrecuencia.add("No")
        val enfermaFrecuencia_seleccionada = enfermaFrecuencia.joinToString(",")


        //presenta problemas psicologicos
        val problemasPsicologicos= mutableListOf<String>()
        if(pscSi.isChecked) problemasPsicologicos.add("Si")
        if(pscNo.isChecked) problemasPsicologicos.add("No")
        val problemasPsicologicos_seleccionada = problemasPsicologicos.joinToString(",")


        //es autosuficiente
        val autosuficiente= mutableListOf<String>()
        if(utsSi.isChecked) autosuficiente.add("Si")
        if(utsNo.isChecked) autosuficiente.add("No")
        val autosuficiente_seleccionada = autosuficiente.joinToString(",")

        //5. peligros ambientales
        //tiene contacto con

        val contactoCon = mutableListOf<String>()
        if(tccPesticidas.isChecked) contactoCon.add("Pesticidad")
        if(tccBioxidoCarbono.isChecked) contactoCon.add("Bióxido de carbono")
        if(tccZonaInsalubre.isChecked) contactoCon.add("Zona insalubre")
        val contactoCon_seleccionada = contactoCon.joinToString(",")

        //es adicto
        val adicto= mutableListOf<String>()
        if(tccaSi.isChecked) adicto.add("Si")
        if(tccaNo.isChecked) adicto.add("No")
        val adicto_seleccionada = adicto.joinToString(",")


        //convive con adictos
        val conviveAdictos= mutableListOf<String>()
        if(tccSi.isChecked) conviveAdictos.add("Si")
        if(tccNo.isChecked) conviveAdictos.add("No")
        val conviveAdictos_seleccionada = conviveAdictos.joinToString(",")
        val convivenciaEspecificar = tccEspecificar.text.toString().trim()


        //adicciones en su entorno o usuarios
        val adiccionesEntorno = mutableListOf<String>()
        if(tcceSi.isChecked) adiccionesEntorno.add("Si")
        if(tcceNo.isChecked) adiccionesEntorno.add("No")
        val adiccionesEntorno_seleccionada = adiccionesEntorno.joinToString(",")


        //cruza las calles con precaucion
        val cruzaCalles= mutableListOf<String>()
        if(crpSi.isChecked) cruzaCalles.add("Si")
        if(crpNo.isChecked) cruzaCalles.add("No")
        val cruzaCalles_seleccionada = cruzaCalles.joinToString(",")

        //utiliza cinturon de seguridad
        val cinturonSeguridad= mutableListOf<String>()
        if(utlSi.isChecked) cinturonSeguridad.add("Si")
        if(utlNo.isChecked) cinturonSeguridad.add("No")
        val cinturonSeguridad_seleccionada = cinturonSeguridad.joinToString(",")


        //cierre las puertas con llave
        val cierraPuerta = mutableListOf<String>()
        if(lavSi.isChecked) cierraPuerta.add("Si")
        if(lavNo.isChecked) cierraPuerta.add("No")
        val cierraPuerta_seleccionada = cierraPuerta.joinToString(",")

        // cierra el tanque de gas
        val cierraGas = mutableListOf<String>()
        if(cgaSi.isChecked) cierraGas.add("Si")
        if(cgaNo.isChecked) cierraGas.add("No")
        val cierraGas_seleccionada = cierraGas.joinToString(",")

        //c) desviacion de la salud
        //requiere de cuidados especificso preventivos y regulares
        val cuidadosPreventivos = mutableListOf<String>()
        if(rpSi.isChecked) cuidadosPreventivos.add("Si")
        if(rpNo.isChecked) cuidadosPreventivos.add("No")
        val cuidadosPreventivos_seleccionada = cuidadosPreventivos.joinToString(",")




        //boton de descargar


        if (nombre.isEmpty() || edad.isEmpty() || sexoId == -1 || fechaIngreso.isEmpty() || religion.isEmpty() || escolaridad.isEmpty() || ocupacion.isEmpty() || estadoCivil.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //RADIOBUTTON
        val sexo = findViewById<RadioButton>(sexoId).text.toString()
        val piso = findViewById<RadioButton>(pisoId).text.toString()
        val ventana = findViewById<RadioButton>(ventanasId).text.toString()
        val luz = findViewById<RadioButton>(luzId).text.toString()
        val noAnimalesDomesticos = findViewById<RadioButton>(animalesDomesticosnoID).text.toString()
        val animalesVacunados = findViewById<RadioButton>(animalesVacunadosID).text.toString()
        val relacionFamiliar = findViewById<RadioButton>(relacionFamiliarID).text.toString()
        val dependenciaEconomica = findViewById<RadioButton>(dependenciaEconomicaID).text.toString()
        val kiSubidoPeso = findViewById<RadioButton>(kisubidoID).text.toString()
        val kpPerdidoPeso = findViewById<RadioButton>(kpPerdidoID).text.toString()
        val acservicio = findViewById<RadioButton>(acservicioID).text.toString()

        val soComeFamilia = findViewById<RadioButton>(comeFamiliaID).text.toString()




        val datosPersonales = hashMapOf(
            "idEstudiante" to idEstudianteBase,
            "Nombre" to nombre,
            "Edad" to edad,
            "Sexo" to sexo,
            "Religion" to religion,
            "Escolaridad" to escolaridad,
            "Ocupacion" to ocupacion,
            "Estado Civil" to estadoCivil,
            "Fecha de Ingreso" to fechaIngreso
        )


        val caracteristicasEntorno = hashMapOf(
            "Piso" to piso,
            "No. Ventanas" to ventana,
            "Pared" to paredSeleccionada,
            "Techo" to techoSeleccionada,
            "Luz" to luz,
            "Abastecimiento de Agua" to aguaSeleccionada,
            "Purificacion de Agua de Consumo" to purificadaAguaSeleccionada,
            "Drenaje y Alcantarillado" to drenajeSeleccionada,
            "Tratamiento de la basura" to basuraSeleccionada,
            "Fauna Nosiva" to faunaSeleccionada,
            "Animales Domesticos " to domesticosSeleccionada,
            "No. Animaeles Domesticos" to noAnimalesDomesticos,
            "Animales Vacunados" to animalesVacunados,

            )
        val patronDeVida = hashMapOf(
            "Relación Familiar" to relacionFamiliar,
            "Ingreso Economico Familiar" to ingresoEconomicoSeleccionado,
            "Dependencia Economica" to dependenciaEconomica,
            "Estado nutricional" to estadoNutricionalSeleccionado,
            "Cabello" to cabelloSeleccionado,
            "Mucosas" to mucosaSeleccionada,
            "Piel" to pielSeleccionada,
            "Labios" to labiosSeleccionada,
            "Encias" to enciasSeleccionada,
            "Nariz y orejas" to narizorejasSaludables,
            "Uñas" to unasSeleccionada,
            "Sistema Óseo" to sistemaOseoSeleccionado,
            "En general" to generalSeleccionado,
            "Variación en el peso en los últimos 6 meses" to vpPesoRespuesta,
            "¿Cuántos Kilos ha subido de peso?" to kiSubidoPeso,
            "¿Cuántos kilos ha perdido de peso?" to kpPerdidoPeso,
            "¿Cómo esta su dentadura?" to dentaduraSeleccionado,
            "¿Presenta algún problema en la cavidad oral?" to cavidadAguaSeleccionada,
            "¿Tiene problemas dentales que le hagan dificil comer?" to problemasDentalesSeleccionada,
            "¿Presenta algún problema para digerir los alimentos?" to digerirAlimentosSeleccionada,
            "¿Qué alimentos no puede comer?" to alimentosnocomenSeleccionada,
            "¿Con qué guisa sus alimentos?" to guisaAlimentosSeleccionada,
            "Come Desayuno" to comeDesayunoSelecccionada,
            "Come en la comida" to comeComidaSeleccionada,
            "¿Qué come en la cena?" to comeCenaSeleccionada,
            "Cepillado de dientes" to cepilladoDientesSeleccionada,
            "Se baña" to seBañaSeleccionada,
            "Cambio de ropa" to cambioRopaSeleccionada,
            "Lavado de manos" to lavadoManosSeleccionada
        )

        val estadoSistemaSalud = hashMapOf(
            "¿Con qué servicios de salud cuenta?" to servicioSaludSeleccionada,
            "¿Cuándo acude al servicio médico?" to acservicio,
            "¿Es capaz de tomar decisiones respecto a su tratamiento?" to tomarDesicionesSeleccionada,
            "¿Tiene presencia de alguna enfermedad?" to presenciaEnfermedadesSeleccionada,
            "¿Tiene tratamiento?" to tieneTratamientoSeleccionada,
            "¿Lleva a cabo su tratamiento?" to llevaTratamietoSeleccionada
        )



        val mantenimientoAire = hashMapOf(
            "Frecuencia Respiratoria" to frrespiratoriasotro,
            "Cianosis" to tieneCianosis_seleccionada,
            "Llenado Capilar" to llenadoCapilar_seleccionada,
            "Fuma" to fuma_seleccionada,
            "Número de cigarrillos al día" to cigarrosDia,
            "Convive con fumadores" to conviveFumadores_seleccionada,
            "Tos Frecuente" to tosFrecuenteSeleccionada,
            "Ruidos respiratorios anormales" to respiracionAnormalSeleccionada,
            "Factores desencadenantes de alteraciones respiratorias" to factoresRespiracion,
            "Oxigenoterapia" to oxigenoterapiaSeleccionada,
           "Padece enfermedades respiratorias/pulmonales" to enf_respiratoriaPulmonalSeleccionada,
            "Frecuencia Cardiaca" to ca_frecuenciaCardiaca,
            "T/A" to ca_TA,
            "PVC" to ca_PVC,
            "Várices" to extremidadesInferiores_seleccionada,
            "Várices clasificación" to extremidades_clasificacion,
            "Várices pulso" to extremidades_pulso,
            "Várices temp" to extremidades_temp,
            "Várices coloración" to extremidades_coloracion,
            "Várices llenado capilar" to extremidades_llenadoCapilar,
            "Edema" to edema_seleccionada


        )

        val mantenimientoAgua = hashMapOf(
            "Consumo habitual de agua aproximadamente por día" to consumoHabitualAgua,
            "Origen del consumo de agua" to consumoAguaSeleccionada,
            "Origen del consumo de agua otro tipo de liquido" to ocotroTipoLiquido,
            "Origen del consumo de agua cantidad en 24 horas" to ocCantidadhoras,
            "Presencia de datos de deshidratación" to deshidratacionSeleccionada,
            "Consumo de alcohol" to consumoAlcoholSeleccionado,
            "Tipo de Producto" to tipoProductoSeleccionado,
            "Uso de apoyos para dormir" to apoyoDormir_seleccionado,
        )

        val mantenimientoAlimento = hashMapOf(
            "Somatometría Peso (kg)" to sopeso,
            "Somatometría Talla (mts)" to sotalla,
            "Somatometría IMC (%)" to soimc,
            "Somatometría Cintura (cm)" to socintura,
            "Somatometría Perímetro abdominal (cm)" to soperimetroabdominal,
            "Somatometría Numero comida al día" to sonumerocomidadia,
            "Somatometría Come con la familia" to soComeFamilia,
            "Ingesta suplementos alimenticios" to ingestaSuplementosSeleccionado,
            "Presencia de" to presenciaDeSeleccionada,
            "Cereales" to getSeleccion(cerealesn, cerealescn, cerealesav, cerealescs, cerealess),
            "Vegetales" to getSeleccion(
                vegetales_n,
                vegetales_cn,
                vegetales_av,
                vegetales_cs,
                vegetales_s
            ),
            "Frutas" to getSeleccion(frutas_n, frutas_cs, frutas_av, frutas_cs, frutas_s),
            "Carnes" to getSeleccion(carnes_n, carnes_cn, carnes_av, carnes_cs, carnes_s),
            "Lácteos" to getSeleccion(lacteos_n, lacteos_cn, lacteos_av, lacteos_cs, lacteos_s),
            "Leguminosas" to getSeleccion(
                leguminosas_n,
                leguminosas_cn,
                leguminosas_av,
                leguminosas_cs,
                leguminosas_s
            ),

            )

        val cuidadosEliminacionExcremento = hashMapOf(
            "Eliminacion vesical (no de veces al dia)" to noVecesDia,
            "Eliminacion vesical (incontinencia)" to eliminacionVesicalIncontinencia_Seleccionada,
            "Eliminacion vesical (retención)" to eliminarRetencion_seleccionada,
            "Eliminacion veical (dolor)" to eliminarDolor_seleccionada,
            "Caracteristicas de la orina" to caracteristicasOrina,
            "Cantidad aprox. en 24 horas" to cantidadOrina,
            "Uso de auxiliares para orina " to auxiliarOrina_seleccionado,
            "Eliminacion intestinal (frecuencia en 24 horas)" to frecuenciaIntestinal,
            "Eliminacion intestinal presencia de" to eliminacionIntestinal_seleccionada,
            "Caracteristica de la evacuacion" to caracteristicaEvacuacion,
            "Uso de auxiliares para evacuar" to auxiliarEvacuar_seleccionado,
            "Secreción transvaginal" to secrecionTransvaginal_seleccionado

        )

        val equilibrioActividadReposo = hashMapOf(
            "Reposo" to reposo,
            "Realiza siesta diurnas" to siestasDiurnas_seleccionada,
            "¿Tiene dificultad para conciliar el sueño? " to dificultadSueno_seleccionada,
            "¿Cómo se encuentra al despertar?" to encuentraDespertar_seleccionado,
            "Presencia de: " to presencia_seleccionado,
            "Despierta por la noche" to despiertaNoche_seleccionado,
            "Realiza una actividad Fisica" to actividadFisica_seleccionado,
            "Necesita apoyo para " to apoyo_seleccionado,
            "Presencia de dolor en " to presenciaDolor_seleccionado,
        )

        val equilibrioSociedad = hashMapOf(
            "Problemas auditivos" to problemasAuditivos_seleccionado,
            "Utiliza apoyo para escuchar" to apoyoEscuchar_seleccionada,
            "Presencia de" to presenciaSistema_seleccionada,
            "Problemas y/o alteracion visual" to alteracionVisual_seleccionado,
            "Utiliza lentes" to utilizaLentes_seleccionada,
            "Tiempo de uso de (lentes, armazón o contacto)" to tiempoUso_seleccionada,
            "Observar presencia de: " to observarPresencia_seleccionado,
            "La mayor parte del tiempo la pasa " to mayorParte_seleccionado,
            "Realiza actividades recreativas" to actividadRecreativa_seleccionado,
            "Cómo considera sus relaciones" to relaciones_seleccionado,
            "Cómo es la relacion con el personal de salud" to personalsalud_seleccionado,


            )
        val bienestarHumano= hashMapOf(
            "Tipo y RH sanguíneo" to tipoSanguineo,
            "Presencia de heridas" to presenciaHeridas_seleccionado,
            "Características del acceso venoso" to accesoVenoso,
            "Cateterismo vesical" to vesical_seleccionado,
        )

        val funcionamientoHumano= hashMapOf(
            "Aceptación de su aspecto físico" to aceptacionFisico_seleccionada,
            "Pertenece a algún grupo social" to grupoSocial_seleccionada,
            "Aceptación de los cambios a partir de su enfermedad" to cambiosEnfermedad_seleccionada,
            "Presencia de alteraciones emocionales" to alteracionEmocionales_seleccionado,
        )

        val procesosVitales = hashMapOf(
            "Edad de IVS" to edadivs,
            "VSA" to vsa_seleccionada,
            "Se protegió en su última relación sexual" to seProtegio_seleccionada,
            "No. de parejas sexuales" to noParejas,
            "Conforme con sus preferencias sexuales" to conformePreferencias_seleccionada,
            "Consideras las relaciones sexuales" to relacionesSexuales_seleccionada,
            "Esquema de vacunacion según edad " to esquemaVacunacion_seleccionado,
            "Esquema de vacunacion según edad (especificar)" to vacunacionEspecificar,


            )

        val mujer = hashMapOf(
            "Edad de (Menarca)" to edmenarca,
            "Edad de (Telarca)" to edtelarca,
            "Edad de (Pubarca)" to edpubarca,
            "Edad de (Menopausia)" to edmenopausia,
            "Características de la menstruación" to menstruacion_seleccionada,
            "Duración de la menstruación " to menstruacionDuracion,
            "Métodos de planificación utilizados" to pometodo,
            "No. de gestas" to gestasmg,
            "Para" to paramg,
            "Aborto" to abortomg,
            "Cesárea" to cesareamg,
            "Nacidos vivos" to nacidosVivosmg,
            "Embarazos de alto riesgo" to altoRiesgo_seleccionada,
            "Cirugías ap. Reproductor" to cirugiaap_seleccionada,
            "Especificar embarazo de alto riesgo o cirugía ap.Reproductor" to ebespecifique,
            "FUM" to noFum,
            "Fecha del último papanicolau" to pfpapanicolau,
            "Resultados del último papanicolau" to pfresultado,
            "Exploración de mama" to exmama,
            "Resultado de la exploración de mama" to exresultado,
        )

        val hombre= hashMapOf(
            "Fecha del último examen de próstata" to exhexamen,
            "Especificación del ultimo examen de prostata " to exhespecifique,
            "Resultado del ultimo examen de prostata " to exhresultado,
            "Alteraciones en aparato reproductor" to alteracionReproductor_seleccionada,
            "Alteraciones en aparato reproductor (Especificada)" to alteracionReproductor_especifique,
            "Asistencia a campañas de salud" to asistenciaCampana_seleccionada,
            "Cómo responde ante situaciones de duelo o pérdida" to situacionesPerdida,
            "Cambio de residencia" to cambioResidencia_seleccionada,
            "Casa" to casa_seleccionada,
            "Trabajo" to trabajo_seleccionada,
            "Familia" to familia_seleccionada,
            "Ingresos" to ingresos_seleccionada,
            "Se enferma con frecuencia" to enfermaFrecuencia_seleccionada,
            "Presenta problemas psicologicos" to problemasPsicologicos_seleccionada,
            "Es autosuficiente" to autosuficiente_seleccionada,
            "Tiene contacto con: " to contactoCon_seleccionada,
            "Es adicto" to adicto_seleccionada,
            "Convive con adictos" to conviveAdictos_seleccionada,
            "Convive con adictos (especificacion)" to convivenciaEspecificar,
            "Adicciones en su entorno" to adiccionesEntorno_seleccionada,
            "Cruza las calles con precaucion" to cruzaCalles_seleccionada,
            "Utiliza cinturón de seguridad" to cinturonSeguridad_seleccionada,
            "Cierra las puertas con llave" to cierraPuerta_seleccionada,
            "Cierra el tanque de gas" to cierraGas_seleccionada,

            )



        val desviacionSalud= hashMapOf(
            "Requiere de cuidados especificos preventivos y reguladores" to cuidadosPreventivos_seleccionada
        )



        val datosFinales = hashMapOf(
            "Descriptivos de la persona" to datosPersonales,
            "Caracteristicas de entorno" to caracteristicasEntorno,
            "Patron de vida " to patronDeVida,
            "Estado y sistema de salud" to estadoSistemaSalud,
            "Mantenimiento de un aporte de aire suficiente" to mantenimientoAire,
            "Mantenimiento de un aporte de agua suficiente" to mantenimientoAgua,
            "Mantenimiento de un aporte de alimento suficiente" to mantenimientoAlimento,
            "Prevencion de cuidados asociados con los procesos de eliminacion y los excrementos" to cuidadosEliminacionExcremento,
            "Mantenimiento del equilibrio entre la actividad y el reposo" to equilibrioActividadReposo,
            "Restriccion de actividad fisica" to restriccionFisica_seleccionada,
            "Mantenimiento del equilibrio entre la sociedad y la interaccion humana" to equilibrioSociedad,
            "Prevencion de peligros para la vida, el funcionamiento y bienestar humano " to bienestarHumano,
            "Promocion del funcionamiento humano" to funcionamientoHumano,
            "Los que apoyan los procesos vitales" to procesosVitales,
            "Los que apoyan los procesos vitales (Mujeres)" to mujer,
            "Los que apoyan los procesos vitales (Hombres)" to hombre,
            "Desviaciones de la salud" to desviacionSalud
        )



        //val docRef = db.collection("guia-valoracion").document()
        //docRef.collection("Seccion_a").document("Datos_Personales").set(datosPersonales)
        db.collection("guia-valoracion")
            .add(datosFinales)

            .addOnSuccessListener {
                Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }




    // Retorna la selección de una fila
    fun getSeleccion(checkNunca: CheckBox, checkCasiNunca: CheckBox, checkAlgunaVez: CheckBox, checkCasiSiempre: CheckBox, checkSiempre: CheckBox): String {
        return when {
            checkNunca.isChecked -> "Nunca"
            checkCasiNunca.isChecked -> "Casi Nunca"
            checkAlgunaVez.isChecked -> "Alguna Vez"
            checkCasiSiempre.isChecked -> "Casi Siempre"
            checkSiempre.isChecked -> "Siempre"
            else -> ""
        }
    }

    // Limita a una selección por fila
    fun setupSingleSelection(vararg checks: CheckBox) {
        for (check in checks) {
            check.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checks.forEach { if (it != check) it.isChecked = false }
                }
            }
        }
    }


    fun setupExclusiveCheckBox(
        exclusive: CheckBox,
        editText: EditText?,
        others: List<CheckBox>
    ) {
        // Cuando seleccionan el "exclusivo" (ej. Otro)
        exclusive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editText?.isEnabled = true
                others.forEach { it.isChecked = false } // desmarcar todos los demás
            } else {
                editText?.isEnabled = false
                editText?.text?.clear()
            }
        }

        // Cuando seleccionan cualquiera de los "otros"
        others.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    exclusive.isChecked = false
                    editText?.isEnabled = false
                    editText?.text?.clear()
                }
            }
        }

    }







}


