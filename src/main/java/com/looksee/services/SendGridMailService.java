package com.looksee.services;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class SendGridMailService {

    SendGrid sendGrid;

    public SendGridMailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    public void sendMail(String msg) {
        Email from = new Email("bkindred@look-see.com");
        String subject = "Requesting audit report";
        Email to = new Email("support@look-see.com");
        
        sendMail(to, from, subject, msg);
    }
    
    @Deprecated
    public void sendMail(Email to, Email from, String subject, String msg) {      
        Content content = new Content("text/html", msg);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            //Response response = this.sendGrid.api(request);
            sendGrid.api(request);

            // ...
        } catch (IOException ex) {
        	ex.printStackTrace();
            // ...
        }
    }    

	public void sendPageAuditCompleteEmail(String account_email, String page_url, long audit_record_id) {
		Email to = new Email(account_email);
		Email from = new Email("bkindred@look-see.com");
		String subject = "UX audit complete for "+page_url;

		String email_msg = "<html data-editor-version=\"2\" class=\"sg-campaigns\" xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" + 
				"      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" + 
				"      <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1\">\n" + 
				"      <!--[if !mso]><!-->\n" + 
				"      <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\">\n" + 
				"      <!--<![endif]-->\n" + 
				"      <!--[if (gte mso 9)|(IE)]>\n" + 
				"      <xml>\n" + 
				"        <o:OfficeDocumentSettings>\n" + 
				"          <o:AllowPNG/>\n" + 
				"          <o:PixelsPerInch>96</o:PixelsPerInch>\n" + 
				"        </o:OfficeDocumentSettings>\n" + 
				"      </xml>\n" + 
				"      <![endif]-->\n" + 
				"      <!--[if (gte mso 9)|(IE)]>\n" + 
				"  <style type=\"text/css\">\n" + 
				"    body {width: 600px;margin: 0 auto;}\n" + 
				"    table {border-collapse: collapse;}\n" + 
				"    table, td {mso-table-lspace: 0pt;mso-table-rspace: 0pt;}\n" + 
				"    img {-ms-interpolation-mode: bicubic;}\n" + 
				"  </style>\n" + 
				"<![endif]-->\n" + 
				"      <style type=\"text/css\">\n" + 
				"    body, p, div {\n" + 
				"      font-family: arial,helvetica,sans-serif;\n" + 
				"      font-size: 14px;\n" + 
				"    }\n" + 
				"    body {\n" + 
				"      color: #000000;\n" + 
				"    }\n" + 
				"    body a {\n" + 
				"      color: #1188E6;\n" + 
				"      text-decoration: none;\n" + 
				"    }\n" + 
				"    p { margin: 0; padding: 0; }\n" + 
				"    table.wrapper {\n" + 
				"      width:100% !important;\n" + 
				"      table-layout: fixed;\n" + 
				"      -webkit-font-smoothing: antialiased;\n" + 
				"      -webkit-text-size-adjust: 100%;\n" + 
				"      -moz-text-size-adjust: 100%;\n" + 
				"      -ms-text-size-adjust: 100%;\n" + 
				"    }\n" + 
				"    img.max-width {\n" + 
				"      max-width: 100% !important;\n" + 
				"    }\n" + 
				"    .column.of-2 {\n" + 
				"      width: 50%;\n" + 
				"    }\n" + 
				"    .column.of-3 {\n" + 
				"      width: 33.333%;\n" + 
				"    }\n" + 
				"    .column.of-4 {\n" + 
				"      width: 25%;\n" + 
				"    }\n" + 
				"    ul ul ul ul  {\n" + 
				"      list-style-type: disc !important;\n" + 
				"    }\n" + 
				"    ol ol {\n" + 
				"      list-style-type: lower-roman !important;\n" + 
				"    }\n" + 
				"    ol ol ol {\n" + 
				"      list-style-type: lower-latin !important;\n" + 
				"    }\n" + 
				"    ol ol ol ol {\n" + 
				"      list-style-type: decimal !important;\n" + 
				"    }\n" + 
				"    @media screen and (max-width:480px) {\n" + 
				"      .preheader .rightColumnContent,\n" + 
				"      .footer .rightColumnContent {\n" + 
				"        text-align: left !important;\n" + 
				"      }\n" + 
				"      .preheader .rightColumnContent div,\n" + 
				"      .preheader .rightColumnContent span,\n" + 
				"      .footer .rightColumnContent div,\n" + 
				"      .footer .rightColumnContent span {\n" + 
				"        text-align: left !important;\n" + 
				"      }\n" + 
				"      .preheader .rightColumnContent,\n" + 
				"      .preheader .leftColumnContent {\n" + 
				"        font-size: 80% !important;\n" + 
				"        padding: 5px 0;\n" + 
				"      }\n" + 
				"      table.wrapper-mobile {\n" + 
				"        width: 100% !important;\n" + 
				"        table-layout: fixed;\n" + 
				"      }\n" + 
				"      img.max-width {\n" + 
				"        height: auto !important;\n" + 
				"        max-width: 100% !important;\n" + 
				"      }\n" + 
				"      a.bulletproof-button {\n" + 
				"        display: block !important;\n" + 
				"        width: auto !important;\n" + 
				"        font-size: 80%;\n" + 
				"        padding-left: 0 !important;\n" + 
				"        padding-right: 0 !important;\n" + 
				"      }\n" + 
				"      .columns {\n" + 
				"        width: 100% !important;\n" + 
				"      }\n" + 
				"      .column {\n" + 
				"        display: block !important;\n" + 
				"        width: 100% !important;\n" + 
				"        padding-left: 0 !important;\n" + 
				"        padding-right: 0 !important;\n" + 
				"        margin-left: 0 !important;\n" + 
				"        margin-right: 0 !important;\n" + 
				"      }\n" + 
				"      .social-icon-column {\n" + 
				"        display: inline-block !important;\n" + 
				"      }\n" + 
				"    }\n" + 
				"  </style>\n" + 
				"      <!--user entered Head Start--><!--End Head user entered-->\n" + 
				"    </head>\n" + 
				"    <body>\n" + 
				"      <center class=\"wrapper\" data-link-color=\"#1188E6\" data-body-style=\"font-size:14px; font-family:arial,helvetica,sans-serif; color:#000000; background-color:#FFFFFF;\">\n" + 
				"        <div class=\"webkit\">\n" + 
				"          <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"wrapper\" bgcolor=\"#FFFFFF\">\n" + 
				"            <tbody><tr>\n" + 
				"              <td valign=\"top\" bgcolor=\"#FFFFFF\" width=\"100%\">\n" + 
				"                <table width=\"100%\" role=\"content-container\" class=\"outer\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" + 
				"                  <tbody><tr>\n" + 
				"                    <td width=\"100%\">\n" + 
				"                      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" + 
				"                        <tbody><tr>\n" + 
				"                          <td>\n" + 
				"                            <!--[if mso]>\n" + 
				"    <center>\n" + 
				"    <table><tr><td width=\"600\">\n" + 
				"  <![endif]-->\n" + 
				"                                    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%; max-width:600px;\" align=\"center\">\n" + 
				"                                      <tbody><tr>\n" + 
				"                                        <td role=\"modules-container\" style=\"padding:0px 0px 0px 0px; color:#000000; text-align:left;\" bgcolor=\"#FFFFFF\" width=\"100%\" align=\"left\"><table class=\"module preheader preheader-hide\" role=\"module\" data-type=\"preheader\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"display: none !important; mso-hide: all; visibility: hidden; opacity: 0; color: transparent; height: 0; width: 0;\">\n" + 
				"    <tbody><tr>\n" + 
				"      <td role=\"module-content\">\n" + 
				"        <p></p>\n" + 
				"      </td>\n" + 
				"    </tr>\n" + 
				"  </tbody></table><table class=\"wrapper\" role=\"module\" data-type=\"image\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"5ef90933-5f86-4d51-8fcb-d452552ab2d6\">\n" + 
				"    <tbody>\n" + 
				"      <tr>\n" + 
				"        <td style=\"font-size:6px; line-height:10px; padding:0px 0px 0px 0px;\" valign=\"top\" align=\"center\">\n" + 
				"          <img class=\"max-width\" border=\"0\" style=\"display:block; color:#000000; text-decoration:none; font-family:Helvetica, arial, sans-serif; font-size:16px; max-width:30% !important; width:20%; height:auto !important;\" width=\"150\" alt=\"\" data-proportionally-constrained=\"true\" data-responsive=\"true\" src=\"http://cdn.mcauto-images-production.sendgrid.net/096106beea9422a2/d5dd3e1e-1814-4178-88fd-b02f4492876b/2000x1421.png\">\n" + 
				"        </td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table class=\"module\" role=\"module\" data-type=\"text\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"438fb6c4-f82a-4249-9e7f-1f2ee426e027\" data-mc-module-version=\"2019-10-22\">\n" + 
				"    <tbody>\n" + 
				"      <tr>\n" + 
				"        <td style=\"padding:18px 0px 18px 0px; line-height:30px; text-align:inherit;\" height=\"100%\" valign=\"top\" bgcolor=\"\" role=\"module-content\"><div><h2 style=\"text-align: center\">Your audit results for " + page_url + " are ready!</h2><div></div></div></td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"100%\" role=\"module\" data-type=\"columns\" style=\"padding:0px 0px 0px 0px;\" bgcolor=\"#FFFFFF\" data-distribution=\"1\">\n" + 
				"    <tbody>\n" + 
				"      <tr role=\"module-content\">\n" + 
				"        <td height=\"100%\" valign=\"top\"><table width=\"580\" style=\"width:580px; border-spacing:0; border-collapse:collapse; margin:0px 10px 0px 10px;\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"\" class=\"column column-0\">\n" + 
				"      <tbody>\n" + 
				"        <tr>\n" + 
				"          <td style=\"padding:0px;margin:0px;border-spacing:0;\"></td>\n" + 
				"        </tr>\n" + 
				"      </tbody>\n" + 
				"    </table></td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table class=\"module\" role=\"module\" data-type=\"spacer\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"570c9817-1223-40e7-a0cf-37b6c6000021\">\n" + 
				"    <tbody>\n" + 
				"      <tr>\n" + 
				"        <td style=\"padding:0px 0px 30px 0px;\" role=\"module-content\" bgcolor=\"\">\n" + 
				"        </td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"module\" data-role=\"module-button\" data-type=\"button\" role=\"module\" style=\"table-layout:fixed;\" width=\"100%\" data-muid=\"1be69b32-3fed-4c1f-86e3-ad2b912b2c98\">\n" + 
				"      <tbody>\n" + 
				"        <tr>\n" + 
				"          <td align=\"center\" bgcolor=\"\" class=\"outer-td\" style=\"padding:0px 0px 0px 0px;\">\n" + 
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"wrapper-mobile\" style=\"text-align:center;\">\n" + 
				"              <tbody>\n" + 
				"                <tr>\n" + 
				"                <td align=\"center\" bgcolor=\"#333333\" class=\"inner-td\" style=\"border-radius:6px; font-size:16px; text-align:center; background-color:inherit;\">\n" + 
				"                  <a role='button' href=\"https://app.look-see.com/?audit_record_id="+audit_record_id+"\" style=\"background-color:#333333; border:1px solid #333333; border-color:#333333; border-radius:6px; border-width:1px; color:#ffffff; display:inline-block; font-size:14px; font-weight:normal; letter-spacing:0px; line-height:normal; padding:12px 18px 12px 18px; text-align:center; text-decoration:none; border-style:solid;\" target=\"_blank\">View Results</a>\n" + 
				"                </td>\n" + 
				"                </tr>\n" + 
				"              </tbody>\n" + 
				"            </table>\n" + 
				"          </td>\n" + 
				"        </tr>\n" + 
				"      </tbody>\n" + 
				"    </table><div data-role=\"module-unsubscribe\" class=\"module\" role=\"module\" data-type=\"unsubscribe\" style=\"color:#444444; font-size:12px; line-height:20px; padding:16px 16px 16px 16px; text-align:Center;\" data-muid=\"4e838cf3-9892-4a6d-94d6-170e474d21e5\"><div class=\"Unsubscribe--addressLine\"><p class=\"Unsubscribe--senderName\" style=\"font-size:12px; line-height:20px;\"></p><p style=\"font-size:12px; line-height:20px;\"><span class=\"Unsubscribe--senderAddress\"></span>, <span class=\"Unsubscribe--senderCity\"></span>, <span class=\"Unsubscribe--senderState\"></span> <span class=\"Unsubscribe--senderZip\"></span></p></div><p style=\"font-size:12px; line-height:20px;\"><a class=\"Unsubscribe--unsubscribeLink\" href=\"\" target=\"_blank\" style=\"\">Unsubscribe</a> - <a href=\"\" target=\"_blank\" class=\"Unsubscribe--unsubscribePreferences\" style=\"\">Unsubscribe Preferences</a></p></div></td>\n" + 
				"                                      </tr>\n" + 
				"                                    </tbody></table>\n" + 
				"                                    <!--[if mso]>\n" + 
				"                                  </td>\n" + 
				"                                </tr>\n" + 
				"                              </table>\n" + 
				"                            </center>\n" + 
				"                            <![endif]-->\n" + 
				"                          </td>\n" + 
				"                        </tr>\n" + 
				"                      </tbody></table>\n" + 
				"                    </td>\n" + 
				"                  </tr>\n" + 
				"                </tbody></table>\n" + 
				"              </td>\n" + 
				"            </tr>\n" + 
				"          </tbody></table>\n" + 
				"        </div>\n" + 
				"      </center>\n" + 
				"    \n" + 
				"  </body></html>";
    	sendMail(to, from, subject, email_msg);
   	}

	/**
	 * Sends the HTML email for domain audit completion to the given email address
	 * 
	 * @param account_email
	 * @param domain_url
	 * @param domain_id
	 * 
	 * @pre account_email != null
	 * @pre !account_email.isEmpty()
	 * @pre domain_url != null
	 * @pre !account_email.isEmpty()
	 */
	public void sendDomainAuditCompleteEmail(String account_email, String domain_url, long domain_id) {
		assert account_email != null;
		assert !account_email.isEmpty();
		assert domain_url != null;
		assert !domain_url.isEmpty();
		
		Email to = new Email(account_email);
		Email from = new Email("bkindred@look-see.com");
		String subject = "UX audit complete for your domain: "+domain_url;

		String email_msg = "<html data-editor-version=\"2\" class=\"sg-campaigns\" xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" + 
				"      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" + 
				"      <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1\">\n" + 
				"      <!--[if !mso]><!-->\n" + 
				"      <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\">\n" + 
				"      <!--<![endif]-->\n" + 
				"      <!--[if (gte mso 9)|(IE)]>\n" + 
				"      <xml>\n" + 
				"        <o:OfficeDocumentSettings>\n" + 
				"          <o:AllowPNG/>\n" + 
				"          <o:PixelsPerInch>96</o:PixelsPerInch>\n" + 
				"        </o:OfficeDocumentSettings>\n" + 
				"      </xml>\n" + 
				"      <![endif]-->\n" + 
				"      <!--[if (gte mso 9)|(IE)]>\n" + 
				"  <style type=\"text/css\">\n" + 
				"    body {width: 600px;margin: 0 auto;}\n" + 
				"    table {border-collapse: collapse;}\n" + 
				"    table, td {mso-table-lspace: 0pt;mso-table-rspace: 0pt;}\n" + 
				"    img {-ms-interpolation-mode: bicubic;}\n" + 
				"  </style>\n" + 
				"<![endif]-->\n" + 
				"      <style type=\"text/css\">\n" + 
				"    body, p, div {\n" + 
				"      font-family: arial,helvetica,sans-serif;\n" + 
				"      font-size: 14px;\n" + 
				"    }\n" + 
				"    body {\n" + 
				"      color: #000000;\n" + 
				"    }\n" + 
				"    body a {\n" + 
				"      color: #1188E6;\n" + 
				"      text-decoration: none;\n" + 
				"    }\n" + 
				"    p { margin: 0; padding: 0; }\n" + 
				"    table.wrapper {\n" + 
				"      width:100% !important;\n" + 
				"      table-layout: fixed;\n" + 
				"      -webkit-font-smoothing: antialiased;\n" + 
				"      -webkit-text-size-adjust: 100%;\n" + 
				"      -moz-text-size-adjust: 100%;\n" + 
				"      -ms-text-size-adjust: 100%;\n" + 
				"    }\n" + 
				"    img.max-width {\n" + 
				"      max-width: 100% !important;\n" + 
				"    }\n" + 
				"    .column.of-2 {\n" + 
				"      width: 50%;\n" + 
				"    }\n" + 
				"    .column.of-3 {\n" + 
				"      width: 33.333%;\n" + 
				"    }\n" + 
				"    .column.of-4 {\n" + 
				"      width: 25%;\n" + 
				"    }\n" + 
				"    ul ul ul ul  {\n" + 
				"      list-style-type: disc !important;\n" + 
				"    }\n" + 
				"    ol ol {\n" + 
				"      list-style-type: lower-roman !important;\n" + 
				"    }\n" + 
				"    ol ol ol {\n" + 
				"      list-style-type: lower-latin !important;\n" + 
				"    }\n" + 
				"    ol ol ol ol {\n" + 
				"      list-style-type: decimal !important;\n" + 
				"    }\n" + 
				"    @media screen and (max-width:480px) {\n" + 
				"      .preheader .rightColumnContent,\n" + 
				"      .footer .rightColumnContent {\n" + 
				"        text-align: left !important;\n" + 
				"      }\n" + 
				"      .preheader .rightColumnContent div,\n" + 
				"      .preheader .rightColumnContent span,\n" + 
				"      .footer .rightColumnContent div,\n" + 
				"      .footer .rightColumnContent span {\n" + 
				"        text-align: left !important;\n" + 
				"      }\n" + 
				"      .preheader .rightColumnContent,\n" + 
				"      .preheader .leftColumnContent {\n" + 
				"        font-size: 80% !important;\n" + 
				"        padding: 5px 0;\n" + 
				"      }\n" + 
				"      table.wrapper-mobile {\n" + 
				"        width: 100% !important;\n" + 
				"        table-layout: fixed;\n" + 
				"      }\n" + 
				"      img.max-width {\n" + 
				"        height: auto !important;\n" + 
				"        max-width: 100% !important;\n" + 
				"      }\n" + 
				"      a.bulletproof-button {\n" + 
				"        display: block !important;\n" + 
				"        width: auto !important;\n" + 
				"        font-size: 80%;\n" + 
				"        padding-left: 0 !important;\n" + 
				"        padding-right: 0 !important;\n" + 
				"      }\n" + 
				"      .columns {\n" + 
				"        width: 100% !important;\n" + 
				"      }\n" + 
				"      .column {\n" + 
				"        display: block !important;\n" + 
				"        width: 100% !important;\n" + 
				"        padding-left: 0 !important;\n" + 
				"        padding-right: 0 !important;\n" + 
				"        margin-left: 0 !important;\n" + 
				"        margin-right: 0 !important;\n" + 
				"      }\n" + 
				"      .social-icon-column {\n" + 
				"        display: inline-block !important;\n" + 
				"      }\n" + 
				"    }\n" + 
				"  </style>\n" + 
				"      <!--user entered Head Start--><!--End Head user entered-->\n" + 
				"    </head>\n" + 
				"    <body>\n" + 
				"      <center class=\"wrapper\" data-link-color=\"#1188E6\" data-body-style=\"font-size:14px; font-family:arial,helvetica,sans-serif; color:#000000; background-color:#FFFFFF;\">\n" + 
				"        <div class=\"webkit\">\n" + 
				"          <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"wrapper\" bgcolor=\"#FFFFFF\">\n" + 
				"            <tbody><tr>\n" + 
				"              <td valign=\"top\" bgcolor=\"#FFFFFF\" width=\"100%\">\n" + 
				"                <table width=\"100%\" role=\"content-container\" class=\"outer\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" + 
				"                  <tbody><tr>\n" + 
				"                    <td width=\"100%\">\n" + 
				"                      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" + 
				"                        <tbody><tr>\n" + 
				"                          <td>\n" + 
				"                            <!--[if mso]>\n" + 
				"    <center>\n" + 
				"    <table><tr><td width=\"600\">\n" + 
				"  <![endif]-->\n" + 
				"                                    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%; max-width:600px;\" align=\"center\">\n" + 
				"                                      <tbody><tr>\n" + 
				"                                        <td role=\"modules-container\" style=\"padding:0px 0px 0px 0px; color:#000000; text-align:left;\" bgcolor=\"#FFFFFF\" width=\"100%\" align=\"left\"><table class=\"module preheader preheader-hide\" role=\"module\" data-type=\"preheader\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"display: none !important; mso-hide: all; visibility: hidden; opacity: 0; color: transparent; height: 0; width: 0;\">\n" + 
				"    <tbody><tr>\n" + 
				"      <td role=\"module-content\">\n" + 
				"        <p></p>\n" + 
				"      </td>\n" + 
				"    </tr>\n" + 
				"  </tbody></table><table class=\"wrapper\" role=\"module\" data-type=\"image\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"5ef90933-5f86-4d51-8fcb-d452552ab2d6\">\n" + 
				"    <tbody>\n" + 
				"      <tr>\n" + 
				"        <td style=\"font-size:6px; line-height:10px; padding:0px 0px 0px 0px;\" valign=\"top\" align=\"center\">\n" + 
				"          <img class=\"max-width\" border=\"0\" style=\"display:block; color:#000000; text-decoration:none; font-family:Helvetica, arial, sans-serif; font-size:16px; max-width:30% !important; width:30%; height:auto !important;\" width=\"180\" alt=\"\" data-proportionally-constrained=\"true\" data-responsive=\"true\" src=\"http://cdn.mcauto-images-production.sendgrid.net/096106beea9422a2/d5dd3e1e-1814-4178-88fd-b02f4492876b/2000x1421.png\">\n" + 
				"        </td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table class=\"module\" role=\"module\" data-type=\"text\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"438fb6c4-f82a-4249-9e7f-1f2ee426e027\" data-mc-module-version=\"2019-10-22\">\n" + 
				"    <tbody>\n" + 
				"      <tr>\n" + 
				"        <td style=\"padding:18px 0px 18px 0px; line-height:30px; text-align:inherit;\" height=\"100%\" valign=\"top\" bgcolor=\"\" role=\"module-content\"><div><h2 style=\"text-align: center\">Your audit results for " + domain_url + " are ready!</h2><div></div></div></td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"100%\" role=\"module\" data-type=\"columns\" style=\"padding:0px 0px 0px 0px;\" bgcolor=\"#FFFFFF\" data-distribution=\"1\">\n" + 
				"    <tbody>\n" + 
				"      <tr role=\"module-content\">\n" + 
				"        <td height=\"100%\" valign=\"top\"><table width=\"580\" style=\"width:580px; border-spacing:0; border-collapse:collapse; margin:0px 10px 0px 10px;\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"\" class=\"column column-0\">\n" + 
				"      <tbody>\n" + 
				"        <tr>\n" + 
				"          <td style=\"padding:0px;margin:0px;border-spacing:0;\"></td>\n" + 
				"        </tr>\n" + 
				"      </tbody>\n" + 
				"    </table></td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table class=\"module\" role=\"module\" data-type=\"spacer\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"570c9817-1223-40e7-a0cf-37b6c6000021\">\n" + 
				"    <tbody>\n" + 
				"      <tr>\n" + 
				"        <td style=\"padding:0px 0px 30px 0px;\" role=\"module-content\" bgcolor=\"\">\n" + 
				"        </td>\n" + 
				"      </tr>\n" + 
				"    </tbody>\n" + 
				"  </table><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"module\" data-role=\"module-button\" data-type=\"button\" role=\"module\" style=\"table-layout:fixed;\" width=\"100%\" data-muid=\"1be69b32-3fed-4c1f-86e3-ad2b912b2c98\">\n" + 
				"      <tbody>\n" + 
				"        <tr>\n" + 
				"          <td align=\"center\" bgcolor=\"\" class=\"outer-td\" style=\"padding:0px 0px 0px 0px;\">\n" + 
				"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"wrapper-mobile\" style=\"text-align:center;\">\n" + 
				"              <tbody>\n" + 
				"                <tr>\n" + 
				"                <td align=\"center\" bgcolor=\"#333333\" class=\"inner-td\" style=\"border-radius:6px; font-size:16px; text-align:center; background-color:inherit;\">\n" + 
				"                  <a role='button' href=\"https://app.look-see.com/domains/"+domain_id+"\" style=\"background-color:#333333; border:1px solid #333333; border-color:#333333; border-radius:6px; border-width:1px; color:#ffffff; display:inline-block; font-size:14px; font-weight:normal; letter-spacing:0px; line-height:normal; padding:12px 18px 12px 18px; text-align:center; text-decoration:none; border-style:solid;\" target=\"_blank\">View Results</a>\n" + 
				"                </td>\n" + 
				"                </tr>\n" + 
				"              </tbody>\n" + 
				"            </table>\n" + 
				"          </td>\n" + 
				"        </tr>\n" + 
				"      </tbody>\n" + 
				"    </table><div data-role=\"module-unsubscribe\" class=\"module\" role=\"module\" data-type=\"unsubscribe\" style=\"color:#444444; font-size:12px; line-height:20px; padding:16px 16px 16px 16px; text-align:Center;\" data-muid=\"4e838cf3-9892-4a6d-94d6-170e474d21e5\"><div class=\"Unsubscribe--addressLine\"><p class=\"Unsubscribe--senderName\" style=\"font-size:12px; line-height:20px;\"></p><p style=\"font-size:12px; line-height:20px;\"><span class=\"Unsubscribe--senderAddress\"></span>, <span class=\"Unsubscribe--senderCity\"></span>, <span class=\"Unsubscribe--senderState\"></span> <span class=\"Unsubscribe--senderZip\"></span></p></div><p style=\"font-size:12px; line-height:20px;\"><a class=\"Unsubscribe--unsubscribeLink\" href=\"\" target=\"_blank\" style=\"\">Unsubscribe</a> - <a href=\"\" target=\"_blank\" class=\"Unsubscribe--unsubscribePreferences\" style=\"\">Unsubscribe Preferences</a></p></div></td>\n" + 
				"                                      </tr>\n" + 
				"                                    </tbody></table>\n" + 
				"                                    <!--[if mso]>\n" + 
				"                                  </td>\n" + 
				"                                </tr>\n" + 
				"                              </table>\n" + 
				"                            </center>\n" + 
				"                            <![endif]-->\n" + 
				"                          </td>\n" + 
				"                        </tr>\n" + 
				"                      </tbody></table>\n" + 
				"                    </td>\n" + 
				"                  </tr>\n" + 
				"                </tbody></table>\n" + 
				"              </td>\n" + 
				"            </tr>\n" + 
				"          </tbody></table>\n" + 
				"        </div>\n" + 
				"      </center>\n" + 
				"    \n" + 
				"  </body></html>";
    	sendMail(to, from, subject, email_msg);
   	}

}
