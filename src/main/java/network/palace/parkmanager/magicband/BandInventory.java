package network.palace.parkmanager.magicband;

/**
 * <p>The <code>BandInventory</code> enumeration represents various categories and functionalities
 * in a theme park application, such as menus, attractions, and user customization options.</p>
 *
 * <p>This enumeration is organized into several main categories, including:</p>
 * <ul>
 *   <li>Main Menu functionalities</li>
 *   <li>Attractions Menu options</li>
 *   <li>Customization features for magic bands</li>
 *   <li>Profile-related information</li>
 *   <li>Show-related details</li>
 * </ul>
 *
 * <p>Each enumerated constant defines a specific feature or functionality across the application,
 * allowing for structured and organized access to the various components of the system.</p>
 *
 * <p>The constants in this enumeration are grouped as follows:</p>
 * <ul>
 *   <li><b>Main Menu:</b>
 *     <ul>
 *       <li>MAIN</li>
 *       <li>FOOD</li>
 *       <li>SHOWS</li>
 *       <li>ATTRACTION_MENU</li>
 *       <li>PARKS</li>
 *       <li>SHOP</li>
 *       <li>WARDROBE</li>
 *       <li>PROFILE</li>
 *       <li>VISIBILITY</li>
 *       <li>CUSTOMIZE_BAND</li>
 *       <li>PLAYER_TIME</li>
 *       <li>STORAGE_UPGRADE</li>
 *       <li>SERVER_LIST</li>
 *       <li>VIRTUAL_QUEUES</li>
 *     </ul>
 *   </li>
 *   <li><b>Attractions Menu:</b>
 *     <ul>
 *       <li>ATTRACTION_LIST</li>
 *       <li>WAIT_TIMES</li>
 *     </ul>
 *   </li>
 *   <li><b>MagicBand Customization:</b>
 *     <ul>
 *       <li>CUSTOMIZE_BAND_TYPE</li>
 *       <li>CUSTOMIZE_BAND_NAME</li>
 *     </ul>
 *   </li>
 *   <li><b>Profile:</b>
 *     <ul>
 *       <li>RIDE_COUNTERS</li>
 *       <li>RIDE_PHOTOS</li>
 *     </ul>
 *   </li>
 *   <li><b>Show Timetable:</b>
 *     <ul>
 *       <li>TIMETABLE</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>This enumeration aids in organizing and managing various features within the application efficiently.</p>
 */
public enum BandInventory {
    // Main Menu
    MAIN, FOOD, SHOWS, ATTRACTION_MENU, PARKS, SHOP, WARDROBE, PROFILE, VISIBILITY, CUSTOMIZE_BAND, PLAYER_TIME, STORAGE_UPGRADE, SERVER_LIST, VIRTUAL_QUEUES,
    // Attractions Menu
    ATTRACTION_LIST, WAIT_TIMES,
    // Customize MagicBand
    CUSTOMIZE_BAND_TYPE, CUSTOMIZE_BAND_NAME,
    // Profile
    RIDE_COUNTERS, RIDE_PHOTOS,
    // Show Timetable
    TIMETABLE
}
