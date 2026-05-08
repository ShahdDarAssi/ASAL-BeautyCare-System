import java.util.Date;

public class Archive {

	private int archiveId;
	private Date archivedDate;
	private String dataType;

	public Archive() {
	}

	public Archive(int archiveId, Date archivedDate, String dataType) {
		super();
		this.archiveId = archiveId;
		this.archivedDate = archivedDate;
		this.dataType = dataType;
	}

	public int getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(int archiveId) {
		this.archiveId = archiveId;
	}

	public Date getArchivedDate() {
		return archivedDate;
	}

	public void setArchivedDate(Date archivedDate) {
		this.archivedDate = archivedDate;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
