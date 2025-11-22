data class DistrictDto(
    val id: Int,
    val name: String
)

data class ProvinceDto(
    val id: Int,
    val name: String,
    val districts: List<DistrictDto>
)

data class LocationItem(
    val city: String,
    val district: String
)
