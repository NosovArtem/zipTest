package zipconcurrent;

import java.io.IOException;

public enum Commands {

    ZIP {
        @Override
        public void command(ZipOperations zipOperations) throws ZipCommandNotFoundException, IOException {
            zipOperations.zipFile();
        }
    },
    UNZIP {
        @Override
        public void command(ZipOperations zipOperations) throws ZipCommandNotFoundException, IOException {
            zipOperations.unZipFile();
        }
    },
    Z {
        @Override
        public void command(ZipOperations zipOperations) throws ZipCommandNotFoundException, IOException {
            zipOperations.zipFile();
        }
    },
    U {
        @Override
        public void command(ZipOperations zipOperations) throws ZipCommandNotFoundException, IOException {
            zipOperations.unZipFile();
        }
    };

    public abstract void command(ZipOperations zipOperations) throws ZipCommandNotFoundException, IOException;
}

