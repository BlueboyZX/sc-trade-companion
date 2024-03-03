package tools.sctrade.companion.domain.commodity;

import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.sctrade.companion.domain.notification.NotificationService;
import tools.sctrade.companion.domain.user.UserService;
import tools.sctrade.companion.exceptions.NoListingsException;
import tools.sctrade.companion.utils.LocalizationUtil;

public class CommoditySubmissionFactory {
  private final Logger logger = LoggerFactory.getLogger(CommoditySubmissionFactory.class);

  private UserService userService;
  private NotificationService notificationService;
  private CommodityListingFactory commodityListingFactory;
  private CommodityLocationReader commodityLocationReader;

  public CommoditySubmissionFactory(UserService userService,
      NotificationService notificationService, CommodityLocationReader commodityLocationReader,
      CommodityListingFactory commodityListingFactory) {
    this.userService = userService;
    this.notificationService = notificationService;
    this.commodityLocationReader = commodityLocationReader;
    this.commodityListingFactory = commodityListingFactory;
  }

  CommoditySubmission build(BufferedImage screenCapture) {
    var location = commodityLocationReader.read(screenCapture);

    if (location.isEmpty()) {
      notificationService.warn(LocalizationUtil.get("warnNoLocation"));
    }

    var listings = commodityListingFactory.build(screenCapture, location.orElse(null));

    if (listings.isEmpty()) {
      throw new NoListingsException();
    }

    return new CommoditySubmission(userService.get(), listings);
  }
}
