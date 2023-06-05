##Customizing the AEM Grid System

An example grid.less file is included in this clientlib but it currently is not being used. The grid.less file holds all the configurations to the grid system that AEM uses for responsive design. This file creates all the necessary css classes for Layout Mode.

The grid.less file is based on /libs/wcm/foundation/components/page/responsive/css/base.less file.

If you would like to use and customize this grid.less file, you must:

  1. add the grid.less to the css.txt file
  2. You must also remove the category name, 'wcm.foundation.components.page.responsive' from your template Page Policy located in the Template Editor > Page Information > Page Policy

This will remove the default grid system from the template.