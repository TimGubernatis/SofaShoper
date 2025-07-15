package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.clip


data class Country(val code: String, val name: String) {
    val flag: String
        get() {

            return code.uppercase().map { char ->
                0x1F1E6 - 'A'.code + char.code
            }.joinToString("") { Character.toChars(it).concatToString() }
        }
}


val allCountries = listOf(
    Country("AF", "Afghanistan"),
    Country("AL", "Albanien"),
    Country("DZ", "Algerien"),
    Country("AD", "Andorra"),
    Country("AO", "Angola"),
    Country("AG", "Antigua und Barbuda"),
    Country("AR", "Argentinien"),
    Country("AM", "Armenien"),
    Country("AU", "Australien"),
    Country("AT", "Österreich"),
    Country("AZ", "Aserbaidschan"),
    Country("BS", "Bahamas"),
    Country("BH", "Bahrain"),
    Country("BD", "Bangladesch"),
    Country("BB", "Barbados"),
    Country("BY", "Belarus"),
    Country("BE", "Belgien"),
    Country("BZ", "Belize"),
    Country("BJ", "Benin"),
    Country("BT", "Bhutan"),
    Country("BO", "Bolivien"),
    Country("BA", "Bosnien und Herzegowina"),
    Country("BW", "Botsuana"),
    Country("BR", "Brasilien"),
    Country("BN", "Brunei"),
    Country("BG", "Bulgarien"),
    Country("BF", "Burkina Faso"),
    Country("BI", "Burundi"),
    Country("CV", "Cabo Verde"),
    Country("KH", "Kambodscha"),
    Country("CM", "Kamerun"),
    Country("CA", "Kanada"),
    Country("CF", "Zentralafrikanische Republik"),
    Country("TD", "Tschad"),
    Country("CL", "Chile"),
    Country("CN", "China"),
    Country("CO", "Kolumbien"),
    Country("KM", "Komoren"),
    Country("CD", "Kongo (Demokratische Republik)"),
    Country("CG", "Kongo (Republik)"),
    Country("CR", "Costa Rica"),
    Country("CI", "Côte d'Ivoire"),
    Country("HR", "Kroatien"),
    Country("CU", "Kuba"),
    Country("CY", "Zypern"),
    Country("CZ", "Tschechien"),
    Country("DK", "Dänemark"),
    Country("DJ", "Dschibuti"),
    Country("DM", "Dominica"),
    Country("DO", "Dominikanische Republik"),
    Country("EC", "Ecuador"),
    Country("EG", "Ägypten"),
    Country("SV", "El Salvador"),
    Country("GQ", "Äquatorialguinea"),
    Country("ER", "Eritrea"),
    Country("EE", "Estland"),
    Country("SZ", "Eswatini"),
    Country("ET", "Äthiopien"),
    Country("FJ", "Fidschi"),
    Country("FI", "Finnland"),
    Country("FR", "Frankreich"),
    Country("GA", "Gabun"),
    Country("GM", "Gambia"),
    Country("GE", "Georgien"),
    Country("DE", "Deutschland"),
    Country("GH", "Ghana"),
    Country("GR", "Griechenland"),
    Country("GD", "Grenada"),
    Country("GT", "Guatemala"),
    Country("GN", "Guinea"),
    Country("GW", "Guinea-Bissau"),
    Country("GY", "Guyana"),
    Country("HT", "Haiti"),
    Country("HN", "Honduras"),
    Country("HU", "Ungarn"),
    Country("IS", "Island"),
    Country("IN", "Indien"),
    Country("ID", "Indonesien"),
    Country("IR", "Iran"),
    Country("IQ", "Irak"),
    Country("IE", "Irland"),
    Country("IL", "Israel"),
    Country("IT", "Italien"),
    Country("JM", "Jamaika"),
    Country("JP", "Japan"),
    Country("JO", "Jordanien"),
    Country("KZ", "Kasachstan"),
    Country("KE", "Kenia"),
    Country("KI", "Kiribati"),
    Country("KW", "Kuwait"),
    Country("KG", "Kirgisistan"),
    Country("LA", "Laos"),
    Country("LV", "Lettland"),
    Country("LB", "Libanon"),
    Country("LS", "Lesotho"),
    Country("LR", "Liberia"),
    Country("LY", "Libyen"),
    Country("LI", "Liechtenstein"),
    Country("LT", "Litauen"),
    Country("LU", "Luxemburg"),
    Country("MG", "Madagaskar"),
    Country("MW", "Malawi"),
    Country("MY", "Malaysia"),
    Country("MV", "Malediven"),
    Country("ML", "Mali"),
    Country("MT", "Malta"),
    Country("MH", "Marshallinseln"),
    Country("MR", "Mauretanien"),
    Country("MU", "Mauritius"),
    Country("MX", "Mexiko"),
    Country("FM", "Mikronesien"),
    Country("MD", "Moldau"),
    Country("MC", "Monaco"),
    Country("MN", "Mongolei"),
    Country("ME", "Montenegro"),
    Country("MA", "Marokko"),
    Country("MZ", "Mosambik"),
    Country("MM", "Myanmar"),
    Country("NA", "Namibia"),
    Country("NR", "Nauru"),
    Country("NP", "Nepal"),
    Country("NL", "Niederlande"),
    Country("NZ", "Neuseeland"),
    Country("NI", "Nicaragua"),
    Country("NE", "Niger"),
    Country("NG", "Nigeria"),
    Country("MK", "Nordmazedonien"),
    Country("NO", "Norwegen"),
    Country("OM", "Oman"),
    Country("PK", "Pakistan"),
    Country("PW", "Palau"),
    Country("PS", "Palästina"),
    Country("PA", "Panama"),
    Country("PG", "Papua-Neuguinea"),
    Country("PY", "Paraguay"),
    Country("PE", "Peru"),
    Country("PH", "Philippinen"),
    Country("PL", "Polen"),
    Country("PT", "Portugal"),
    Country("QA", "Katar"),
    Country("RO", "Rumänien"),
    Country("RU", "Russland"),
    Country("RW", "Ruanda"),
    Country("KN", "St. Kitts und Nevis"),
    Country("LC", "St. Lucia"),
    Country("VC", "St. Vincent und die Grenadinen"),
    Country("WS", "Samoa"),
    Country("SM", "San Marino"),
    Country("ST", "São Tomé und Príncipe"),
    Country("SA", "Saudi-Arabien"),
    Country("SN", "Senegal"),
    Country("RS", "Serbien"),
    Country("SC", "Seychellen"),
    Country("SL", "Sierra Leone"),
    Country("SG", "Singapur"),
    Country("SK", "Slowakei"),
    Country("SI", "Slowenien"),
    Country("SB", "Salomonen"),
    Country("SO", "Somalia"),
    Country("ZA", "Südafrika"),
    Country("KR", "Südkorea"),
    Country("SS", "Südsudan"),
    Country("ES", "Spanien"),
    Country("LK", "Sri Lanka"),
    Country("SD", "Sudan"),
    Country("SR", "Suriname"),
    Country("SE", "Schweden"),
    Country("CH", "Schweiz"),
    Country("SY", "Syrien"),
    Country("TW", "Taiwan"),
    Country("TJ", "Tadschikistan"),
    Country("TZ", "Tansania"),
    Country("TH", "Thailand"),
    Country("TL", "Timor-Leste"),
    Country("TG", "Togo"),
    Country("TO", "Tonga"),
    Country("TT", "Trinidad und Tobago"),
    Country("TN", "Tunesien"),
    Country("TR", "Türkei"),
    Country("TM", "Turkmenistan"),
    Country("TV", "Tuvalu"),
    Country("UG", "Uganda"),
    Country("UA", "Ukraine"),
    Country("AE", "Vereinigte Arabische Emirate"),
    Country("GB", "Vereinigtes Königreich"),
    Country("US", "USA"),
    Country("UY", "Uruguay"),
    Country("UZ", "Usbekistan"),
    Country("VU", "Vanuatu"),
    Country("VA", "Vatikanstadt"),
    Country("VE", "Venezuela"),
    Country("VN", "Vietnam"),
    Country("YE", "Jemen"),
    Country("ZM", "Sambia"),
    Country("ZW", "Simbabwe")
).sortedBy { it.name }

@Composable
fun CountryPicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = allCountries.find { it.name == value } ?: allCountries.first { it.code == "DE" }
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selected.flag)
            Spacer(Modifier.width(8.dp))
            Text(selected.name, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            allCountries.forEach { country ->
                DropdownMenuItem(
                    text = {
                        Row {
                            Text(country.flag)
                            Spacer(Modifier.width(8.dp))
                            Text(country.name)
                        }
                    },
                    onClick = {
                        onValueChange(country.name)
                        expanded = false
                    }
                )
            }
        }
    }
} 