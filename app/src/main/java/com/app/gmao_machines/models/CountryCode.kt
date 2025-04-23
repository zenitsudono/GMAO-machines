package com.app.gmao_machines.models

data class CountryCode(
    val name: String,
    val code: String,
    val dialCode: String
) {
    override fun toString(): String = "$name (+$dialCode)"
    
    companion object {
        val COUNTRIES = listOf(
            CountryCode("Tunisia", "TN", "216"),
            CountryCode("Morocco", "MA", "212"),
            CountryCode("Algeria", "DZ", "213"),
            CountryCode("France", "FR", "33"),
            CountryCode("United States", "US", "1"),
            CountryCode("United Kingdom", "GB", "44"),
            CountryCode("Germany", "DE", "49"),
            CountryCode("Italy", "IT", "39"),
            CountryCode("Spain", "ES", "34"),
            CountryCode("Canada", "CA", "1"),
            CountryCode("Belgium", "BE", "32"),
            CountryCode("Switzerland", "CH", "41"),
            CountryCode("Egypt", "EG", "20"),
            CountryCode("Libya", "LY", "218"),
            CountryCode("Senegal", "SN", "221"),
            CountryCode("Ivory Coast", "CI", "225"),
            CountryCode("United Arab Emirates", "AE", "971"),
            CountryCode("Saudi Arabia", "SA", "966"),
            CountryCode("Qatar", "QA", "974"),
            CountryCode("China", "CN", "86"),
            CountryCode("Japan", "JP", "81"),
            CountryCode("South Korea", "KR", "82"),
            CountryCode("India", "IN", "91"),
            CountryCode("Brazil", "BR", "55"),
            CountryCode("Mexico", "MX", "52")
        )
        
        // Default to Tunisia
        val DEFAULT = COUNTRIES.first()
        
        fun findByDialCode(dialCode: String): CountryCode {
            return COUNTRIES.find { it.dialCode == dialCode } ?: DEFAULT
        }
        
        fun extractCountryCodeFromPhone(phoneNumber: String): Pair<CountryCode, String> {
            // If the phone starts with +, try to extract the country code
            if (phoneNumber.startsWith("+")) {
                for (country in COUNTRIES) {
                    if (phoneNumber.startsWith("+" + country.dialCode)) {
                        val localNumber = phoneNumber.substring(country.dialCode.length + 1)
                        return Pair(country, localNumber)
                    }
                }
            }
            
            // If no match or no + prefix, return default country code and original number
            return Pair(DEFAULT, phoneNumber)
        }
    }
} 