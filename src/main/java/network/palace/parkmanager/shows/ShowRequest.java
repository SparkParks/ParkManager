package network.palace.parkmanager.shows;

import lombok.Getter;

import java.util.UUID;

/**
 * Represents a request to initiate or interact with a specific show.
 * <p>
 * This class encapsulates information about a show request, including its
 * unique identifiers, the associated show entry, a command, and its approval status.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code requestId} - A unique identifier for the show request.</li>
 *   <li>{@code uuid} - A unique identifier typically associated with the user or initiator of the request.</li>
 *   <li>{@code show} - An instance of {@code ShowEntry}, representing the specific show for which the request is made.</li>
 *   <li>{@code command} - A command string tied to the show, derived from the {@code ShowEntry} instance.</li>
 *   <li>{@code canBeApproved} - A boolean flag indicating whether the request can be approved.</li>
 * </ul>
 *
 * <p><b>Constructor Details:</b></p>
 * <ul>
 *   <li>
 *     {@code ShowRequest(UUID requestId, UUID uuid, ShowEntry show)}:
 *     <ul>
 *       <li>Initializes a new instance of {@code ShowRequest} with a unique request ID, user UUID,
 *           and the associated {@code ShowEntry}.</li>
 *       <li>Sets the {@code command} field by extracting the command from the associated {@code ShowEntry} instance.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Methods:</b></p>
 * <ul>
 *   <li>
 *     {@code setCanBeApproved(boolean canBeApproved)}:
 *     <ul>
 *       <li>Allows modification of the {@code canBeApproved} flag to indicate
 *           whether the show request is eligible for approval.</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Getter
public class ShowRequest {
    /**
     * Represents a unique identifier for the show request.
     * <p>
     * This field is used to uniquely identify each instance of a show request within the system.
     * It ensures that each request can be tracked and managed independently.
     * </p>
     * <p><b>Key Information:</b></p>
     * <ul>
     *   <li>Data Type: {@link UUID}</li>
     *   <li>Purpose: Provides a globally unique identifier for the specific request.</li>
     * </ul>
     * <p><b>Usage Context:</b></p>
     * <ul>
     *   <li>Serves as a primary identifier for handling show requests in the system.</li>
     *   <li>Used to tie related actions or data to the specific request it pertains to.</li>
     * </ul>
     */
    private UUID requestId;

    /**
     * A unique identifier typically associated with the user or initiator of a show request.
     *
     * <p>This field is used to represent and distinguish individual users or entities involved
     * in a {@code ShowRequest}. The UUID provides a globally unique value that ensures
     * each request can be reliably tracked and identified.</p>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Links a specific user or external entity to the {@code ShowRequest} instance.</li>
     *   <li>Can be used for auditing, tracking, or user-specific operations associated
     *       with the request.</li>
     * </ul>
     *
     * <p><b>Notes:</b></p>
     * <ul>
     *   <li>The value is immutable and set during the construction of the {@code ShowRequest} object.</li>
     *   <li>Serves as a secondary identifier, complementing the {@code requestId} field.</li>
     * </ul>
     */
    private UUID uuid;

    /**
     * Represents the specific show entry associated with the show request.
     *
     * <p>This field holds an instance of {@code ShowEntry}, containing information about
     * the show that the request pertains to. The {@code show} field provides details such as
     * the region, display name, command, and associated data value for the show.</p>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Provides detailed information about the requested show.</li>
     *   <li>Enables access to metadata, such as the associated command or display name, required for processing the request.</li>
     *   <li>Facilitates interactions tied to the specific show entry, including executing commands or generating visual
     *       representations (e.g., {@link ItemStack}).</li>
     * </ul>
     *
     * <p>The {@code ShowEntry} instance is initialized when creating a {@code ShowRequest} object
     * and remains associated with the request for its lifetime.</p>
     */
    private ShowEntry show;

    /**
     * Represents the command associated with the show request.
     * <p>
     * This field is derived from the {@link ShowEntry} instance associated with this request
     * and identifies the specific executable action or instruction tied to the selected show.
     * </p>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Encapsulates the command string related to the show entry.</li>
     *   <li>Facilitates execution or interaction with the associated show functionality.</li>
     * </ul>
     */
    private String command;

    /**
     * Indicates whether the associated show request can be approved.
     *
     * <p>This field represents a boolean flag to determine the eligibility of the current show
     * request for approval. It is used to control the approval flow and ensures that a request
     * meets specific criteria before proceeding further.</p>
     *
     * <p><b>Details:</b></p>
     * <ul>
     *   <li>Defaults to {@code false}, implying that the request is initially not eligible for approval.</li>
     *   <li>Modifiable through the {@code setCanBeApproved(boolean canBeApproved)} method to update its state.</li>
     *   <li>Acts as a control mechanism for workflows or validations related to show requests.</li>
     * </ul>
     */
    private boolean canBeApproved = false;

    /**
     * Constructs a new {@code ShowRequest} instance with the provided details.
     *
     * <p>This constructor initializes a {@code ShowRequest} with the specified
     * request ID, user ID, and associated show entry. It also retrieves and sets
     * the command associated with the show entry.</p>
     *
     * @param requestId The unique identifier for this show request. It represents
     *                  the specific request being processed.
     * @param uuid The unique identifier associated with the user making the request.
     *             This ties the request to a specific user or system entity.
     * @param show The {@code ShowEntry} object containing details about the show
     *             being requested, including its region, display name, and command.
     */
    public ShowRequest(UUID requestId, UUID uuid, ShowEntry show) {
        this.requestId = requestId;
        this.uuid = uuid;
        this.show = show;
        this.command = show.getCommand();
    }

    /**
     * Sets whether the show request can be approved or not.
     * <p>
     * This method is used to define if the current show request is permitted
     * to be approved. When {@code true}, the request indicates that the required
     * conditions for approval are satisfied. When {@code false}, the request may
     * still require adjustments or additional inputs before it can be approved.
     * </p>
     *
     * @param canBeApproved A boolean value indicating if the show request can
     *                       be approved. {@code true} allows approval, while
     *                       {@code false} restricts it.
     */
    public void setCanBeApproved(boolean canBeApproved) {
        this.canBeApproved = canBeApproved;
    }
}
