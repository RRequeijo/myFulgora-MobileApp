package com.example.myfulgora.ui.screens.tabs.map

// Um objeto (Singleton) para guardar todos os estilos de mapa que possas vir a ter
object MapStyles {
    const val MapStyleDark = """
    [
      { "elementType": "geometry", "stylers": [{ "color": "#212121" }] },
      { "elementType": "labels.icon", "stylers": [{ "visibility": "off" }] },
      { "elementType": "labels.text.fill", "stylers": [{ "color": "#757575" }] },
      { "elementType": "labels.text.stroke", "stylers": [{ "color": "#212121" }] },
      { "featureType": "administrative", "elementType": "geometry", "stylers": [{ "color": "#757575" }] },
      { "featureType": "poi", "elementType": "labels.text.fill", "stylers": [{ "color": "#757575" }] },
      { "featureType": "road", "elementType": "geometry.fill", "stylers": [{ "color": "#2c2c2c" }] },
      { "featureType": "road", "elementType": "labels.text.fill", "stylers": [{ "color": "#8a8a8a" }] },
      { "featureType": "road.arterial", "elementType": "geometry", "stylers": [{ "color": "#373737" }] },
      { "featureType": "road.highway", "elementType": "geometry", "stylers": [{ "color": "#3c3c3c" }] },
      { "featureType": "water", "elementType": "geometry", "stylers": [{ "color": "#000000" }] }
    ]
    """
}