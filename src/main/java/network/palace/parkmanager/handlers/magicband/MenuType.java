package network.palace.parkmanager.handlers.magicband;

/**
 * The {@code MenuType} enum represents the different types of menus available within the system.
 * These menus enable interaction with various functionalities or items, characterized by their unique types.
 *
 * <p>This enum is typically used in scenarios where the application needs to designate
 * a specific menu type for operations such as rendering a user interface or accessing categorized menu items.
 *
 * <p><b>Menu Types:</b>
 * <ul>
 *   <li>{@code BAND_MAIN}: Represents the primary menu for managing or interacting with bands.</li>
 *   <li>{@code WATCH}: Represents the menu associated with watches or watch-related functionalities.</li>
 *   <li>{@code BACKPACK}: Represents the menu for accessing or managing items stored in a backpack.</li>
 *   <li>{@code LOCKER}: Represents the menu for managing or accessing lockers.</li>
 * </ul>
 *
 * <p><b>Usage Notes:</b>
 * <ul>
 *   <li>Each menu type is uniquely named to correspond to a specific feature or functionality.</li>
 *   <li>Can be used for categorization, user interface rendering, or menu interactions within the application.</li>
 * </ul>
 *
 * <p>This enum enhances clarity and promotes a structured approach to handling menu-related operations.
 */
public enum MenuType {
    BAND_MAIN, WATCH, BACKPACK, LOCKER
}
