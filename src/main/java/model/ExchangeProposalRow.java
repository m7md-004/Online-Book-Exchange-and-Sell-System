package model;

import java.sql.Timestamp;

public class ExchangeProposalRow {
    private int id;
    private int targetListingId;
    private int offeredListingId;
    private int proposerId;
    private String status;
    private Timestamp createdAt;

    private String targetTitle;
    private String offeredTitle;

   
    private boolean targetConfirmed;
    private boolean proposerConfirmed;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTargetListingId() { return targetListingId; }
    public void setTargetListingId(int targetListingId) { this.targetListingId = targetListingId; }

    public int getOfferedListingId() { return offeredListingId; }
    public void setOfferedListingId(int offeredListingId) { this.offeredListingId = offeredListingId; }

    public int getProposerId() { return proposerId; }
    public void setProposerId(int proposerId) { this.proposerId = proposerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getTargetTitle() { return targetTitle; }
    public void setTargetTitle(String targetTitle) { this.targetTitle = targetTitle; }

    public String getOfferedTitle() { return offeredTitle; }
    public void setOfferedTitle(String offeredTitle) { this.offeredTitle = offeredTitle; }

    public boolean isTargetConfirmed() { return targetConfirmed; }
    public void setTargetConfirmed(boolean targetConfirmed) { this.targetConfirmed = targetConfirmed; }

    public boolean isProposerConfirmed() { return proposerConfirmed; }
    public void setProposerConfirmed(boolean proposerConfirmed) { this.proposerConfirmed = proposerConfirmed; }
}
